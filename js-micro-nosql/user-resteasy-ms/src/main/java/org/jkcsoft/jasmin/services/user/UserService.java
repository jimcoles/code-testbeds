package org.jkcsoft.jasmin.services.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
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
@Path("/user")
public class UserService {

    private static Logger log = LoggerFactory.getLogger(UserService.class);

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @Inject
    public UserService(HttpServletRequest request, HttpServletResponse response) {
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

//            mapper.writeValue(response.getOutputStream(), user);
//            ObjectWriter objectWriter = mapper.writer().forType(user.getClass());
//        studentDataMap.put("student", student);
//        // JAVA String
//        studentDataMap.put("name", "Mahesh Kumar");
//        // JAVA Boolean
//        studentDataMap.put("verified", Boolean.FALSE);
//        // Array
//        studentDataMap.put("marks", marks);

//        } catch (JsonParseException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        log.debug("returning: " + user);
        return Response.ok(user).build();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}
