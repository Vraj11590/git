package Scraper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;

import com.jaunt.*;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;
import org.json.*;
import org.json.JSONObject;
import org.json.simple.*;


public class Scraper {
    public static void main(String[] args) throws IOException {
        String websiteURL = "";
        String pageTitle = "";
        Element pageBody = null;
        Elements inputFields = null;
        JSONObject obj = new JSONObject();
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome to Scraper!!");
        System.out.println("--------------------");

        System.out.print("(0) Local\n" +
                "(1) Online\n" +
                "Select: ");
        int choice = scan.nextInt();
        if (choice == 0) {
            System.out.print("Local:  ");
            websiteURL = scan.next();
        } else if (choice == 1) {
            System.out.print("URL (include http:// or https:// ) :  ");
            websiteURL = scan.next();
        }

        obj.put("pageURL", websiteURL);
        try {
            UserAgent userAgent = new UserAgent();
            if (choice == 1) {
                userAgent.visit(websiteURL);
            }
            if (choice == 0) {
                userAgent.open(new File(websiteURL));
            }

            pageBody = userAgent.doc.findFirst("<body>");
            pageTitle = userAgent.doc.findFirst("<title>").getText();
            obj.put("pageTitle", pageTitle);

            //scrapeInputElements(obj, pageBody);
            scrapeLinks(obj, pageBody);
            //scrapeForms(obj,pageBody);
        } catch (JauntException e) {
            System.err.println(e);
        }


        System.out.print(obj);

        try {
            File newFile = new File("C:\\Users\\vpatel\\IdeaProjects\\Java - CoreLib\\src\\Scrapes\\" + pageTitle.substring(0, 5) + ".json");
            if (newFile.createNewFile()) {
                FileWriter writer = new FileWriter(newFile);
                writer.write(obj.toString(4));
                writer.flush(); //??
                writer.close(); //??
            } else {
                System.out.println("Cannot create a scrape file..");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //this works because java passes references to the objects on the heap, so we are making changes to the original object
    //memory management is also done automatically
    static void scrapeInputElements(JSONObject obj, Element pageBody) {
        //find all text fields
        Elements inputFields = pageBody.findEvery("<input>");
        int count = 0;
        for (Element tf : inputFields) {
            JSONObject el = new JSONObject();
            String type = tf.getAttx("type");
            String id = tf.getAttx("id");
            String name = tf.getAttx("name");
            String classname = tf.getAttx("class");

            //create element based on parsed data from dom tree
            el.put("name", name);
            el.put("type", type);
            el.put("id", id);
            el.put("class", classname);


            //for creating a unique id for later retrieval
            String uniqueID = type;
            if (!id.equals(""))
                uniqueID += "_" + id;
            else if (!classname.equals(""))
                uniqueID += "_" + classname;
            else if (!name.equals(""))
                uniqueID += "_" + name;
            else
                uniqueID += "_" + count;
            count++;


            switch (type) {
                case "text": {
                    obj.put(uniqueID, el);
                }
                case "password": {
                    obj.put(uniqueID, el);
                }
                case "button": {
                    obj.put(uniqueID, el);
                }
                case "select": {
                    obj.put(uniqueID, el);
                }
                case "checkbox": {
                    obj.put(uniqueID, el);
                }
                case "radio": {
                    obj.put(uniqueID, el);
                }
                case "submit": {
                    obj.put(uniqueID, el);
                }
                default: {
                    System.out.println("skipped");
                }
            }
        }
    }

    static void scrapeLinks(JSONObject obj, Element pageBody) {
        Elements links = pageBody.findEvery("<a>");
        int count = 0;
        for (Element tf : links) {
            JSONObject el = new JSONObject();
            String type = tf.getAttx("type");
            String id = tf.getAttx("id");
            String name = tf.getAttx("name");
            String classname = tf.getAttx("class");
            String href = tf.getAttx("href");

            //create element based on parsed data from dom tree
            el.put("name", name);
            el.put("type", type);
            el.put("id", id);
            el.put("class", classname);
            el.put("href", href);


            //for creating a unique id for later retrieval
            String uniqueID = type;
            if (!id.equals(""))
                uniqueID += "_" + id;
            else if (!classname.equals(""))
                uniqueID += "_" + classname;
            else if (!name.equals(""))
                uniqueID += "_" + name;
            else
                uniqueID += "_" + tf.getText().replace("/[^a-zA-Z]/g","");
                System.out.println(tf.getText().replace("/[^a-zA-Z]/g",""));
            count++;
            obj.put(uniqueID, el);
        }

    }

    static void scrapeForms(JSONObject obj, Element pageBody)throws JauntException{
        Elements forms = pageBody.findEvery("<form>");
        int count = 0;
        for (Element tf : forms) {
            JSONObject el = new JSONObject();
            System.out.println(tf);

            JSONObject el_submit = new JSONObject();
            Element submit = tf.findFirst("<input type=\"submit\">");
            el_submit.put("name", submit.getAttx("name"));
            el_submit.put("type", submit.getAttx("type"));
            el_submit.put("id", submit.getAttx("id"));
            el_submit.put("class", submit.getAttx("class"));

            el.put("submit",el_submit); //add submit button object

            String type = tf.getAttx("type");
            String id = tf.getAttx("id");
            String name = tf.getAttx("name");
            String classname = tf.getAttx("class");
            String method = tf.getAttx("method");
            String action = tf.getAttx("action");

            //create element based on parsed data from dom tree
            el.put("name", name);
            el.put("type", type);
            el.put("id", id);
            el.put("class", classname);
            el.put("method", method);
            el.put("action", action);


            //for creating a unique id for later retrieval
            String uniqueID = type;
            if (!id.equals(""))
                uniqueID += "_" + id;
            else if (!classname.equals(""))
                uniqueID += "_" + classname;
            else if (!name.equals(""))
                uniqueID += "_" + name;
            else
                uniqueID += "_" + count;
            count++;

            obj.put(uniqueID,el);
        }
    }
}
