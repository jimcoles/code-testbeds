package org.jkcsoft.jasmin.services.userdb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.codehaus.jackson.map.util.JSONWrappedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Sample Secured RESTful Web Service<br>
 *
 * The constructor has Guice injections to enable also Shiro AOP annotations
 *
 * @author pablo.biagioli
 *
 */
@RequestScoped
@Path("/userdb")
public class UserMongoDbService {

    private static Logger log = LoggerFactory.getLogger(UserMongoDbService.class);

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private MongoClient mongoClient;
    private MongoDatabase mongoDb;

    @Inject
    public UserMongoDbService(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @GET
//    @RequiresPermissions("lightsaber:allowed")
//    @RequiresAuthentication
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser() {
        log.debug("in getUser");
        User user = new User();
        user.setFirstName("Jim");
        user.setLastName("Cocogoopwang");
        {
            Address homeAddress = new Address();
            homeAddress.setAddrStreet("1 Pecos St");
            homeAddress.setAddrCityRegion("Austin");
            homeAddress.setRegionCode("TX");
            user.setHomeAddress(homeAddress);
        }
//        try {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> returnDataMap = new HashMap<String, Object>();

        log.debug("returning: " + user);
        return Response.ok(user).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putUser(User user) {
        log.debug("putting: " + user);

        // Retrieving a collection
        MongoCollection<Document> collection = getUserCollection();

//        // Insert a document
//        Document document = new Document("title", "MongoDB")
//            .append("id", 1)
//            .append("description", "database")
//            .append("likes", 100)
//            .append("url", "http://www.tutorialspoint.com/mongodb/")
//            .append("by", "tutorials point");
//        collection.insertOne(document);

        ObjectMapper mapper = new ObjectMapper();;
        Map<String, Object> jsonMap = new HashMap<>();
        Document bsonUserDoc = new Document(jsonMap);
        collection.insertOne(bsonUserDoc);
        System.out.println("Document inserted successfully");
        return Response.ok(user).build();
    }

    public void getStatus() {
        getDbConnection();
    }

    private MongoDatabase getDbConnection() {
        if (mongoClient == null) {
            // TODO add credentials to client connection for security
            MongoCredential credential = MongoCredential.createCredential("sampleUser", "myDb",
                                                                          "password".toCharArray());
            mongoClient = new MongoClient("localhost", 27017);

            log.info("Connected to the database server successfully");
        }

        if (mongoDb == null) {
            mongoDb = mongoClient.getDatabase("myDb");
            log.info("connected to User database");
        }

        return mongoDb;
    }

    private MongoCollection<Document> getUserCollection() {
        return getDbConnection().getCollection("User");
    }

    public void updateUser() {
        getUserCollection().updateOne(Filters.eq("id", 1), Updates.set("likes", 150));
        System.out.println("Document update successfully...");

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public static void main(String[] args) {
        UserMongoDbService service = new UserMongoDbService(null, null);
        service.getDbConnection();
        testLog("successful DB connection test");
    }

    private static void testLog(String s) {
        System.out.println(s);
    }
}