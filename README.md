Modfified version of example available from Redhat at : [https://developers.redhat.com/blog/?p=432287] Orginal Author: Sébastien Blanc 
======================================================================================================================================


Description:
------------
This example shows the minimal code necessary to secure a spring-boot rest endpoint using OAUTH2/Keycloak.
It was necessary to modifiy the orginal in order to show just a rest endpoint and illustrate the OAUTH2 Resource owner grant type.

In a nutshell
-------------

I have a keycloak server that is handling the SSO and acting as the identity provider.

The I have my rest service in spring-boot that looks like this:
@SpringBootApplication
public class ProductAppApplication {
   public static void main(String[] args) {
      SpringApplication.run(ProductAppApplication.class, args);
   }
}

@RestController
class ProductController {

   @RequestMapping(value="/products", method= RequestMethod.GET)
   public String getProducts(){
      return Arrays.asList("iPad","iPhone","iPod").toString();
   }

}

....so nothing out of the ordinary here, (inner class is only there just to show minimalism), not a hint of jwt tokens etc. 
then the OAUTH conf. is provided via the application.props:

#address of the keycloak server
keycloak.auth-server-url=http://localhost:8080/auth
#my keycloak real.
keycloak.realm=SpringBoot
keycloak.resource=product-app
keycloak.public-client=true

server.port=8888
logging.level.=DEBUG

#this gives RBAC - so only allow role of user
keycloak.security-constraints[0].authRoles[0]=user
#lock down any calls to the /products context path.
keycloak.security-constraints[0].securityCollections[0].patterns[0]=/products/*

#uncomment these to use resource owner grant type. else defaults to the Authority Code Grant type.
#keycloak.bearer-only=true
#keycloak.autodetect-bearer-only=true
keycloak.expose-token=true

That is it! - Now my service is locked down using OAUTH2. 
Not even a hint of the world of misery that has been abstracted away from you.

The magic comes from the following:

<dependency>
   <groupId>org.keycloak</groupId>
   <artifactId>keycloak-spring-boot-starter</artifactId>
</dependency>





Steps to setup.
---------------
1. reference Sebastien's blog here for the RH-SSO (Keycloak) setup:
[https://developers.redhat.com/blog/?p=432287]

Once the keycloak setup has been completed then return back to here.


2.  Usecase 1. using the Auhtority Code OAUTH grant type.
----------------------------------------------------------
(this is the end user authentication use case).

ensure the following line is commented in the application.properties file.
    #keycloak.bearer-only=true 
    
3. Now mvn clean install in the root of this project. 

4. Make sure that the RH-SSO server is running and then we should be good to have fun.

5. mvn spring-boot:run in the root of the project this will standup the standalone spring boot rest service.

6. point your browser to the following URL http://localhost:8081/products - the spring boot rest service is projected, the application will 
recognize that their is know valid token and then reroute to the Redhat SSO login screen.

- now login as testuser:password (this user you just setup!), you should be authenticated, given a token passed back to the products method where you can now view the output.

7. because the of the config. line : keycloak.expose-token=true, you can view the token just by adding .../k_query_bearer_token to the URL, perhaps copy the token and then go to jwt.io and decode it just for laughs!

Take a moment to reflect on what just happened.
-----------------------------------------------
Our spring boot service was OAUTH enabled by just a few lines of configuration. Our service implementation was not in any way affected whatsoever.
We have not had to write any of the boiler plate to support the any of the complexity, and we also performed RBAC (role based auth.)



8.  Usecase 2. using the Resource Owner OAUTH grant type.
---------------------------------------------------------
This is typically the machine/service to (trusted) machine/service usecase.
 
9. kill the running service, and delete the cookies from the browser (this is where the token is stored).

10. uncomment the line  #keycloak.bearer-only=true  in application properties, this will then just check for the existence of the valid bearer token.
It will not redirect you.

11. Again, mvn clean install , then mvn spring-boot:run

12. Now once more point your browser to the service on http:localhost:8081/products 

13. Because you don't have a valid token you should see a 401 error - unauthorised.

14. - So now you can get a token and authenticate to the service.
 
NOTE: it is possible to do this via the CURL command line 

    but given that you may have windows 
    and given that you may not have curl, 
    and given that postman is much much easier 
 
- lets just use postman!, so download and install the latest version of the postman application.

SetUp by following my screen shots.

- firstly set up a request to point to the products service [http:localhost:8081/products] - send it and of course it will fail.

- Now set the auth type in postman to be OAUTH2 (see my screen).
- Now click on get access token and follow my other screen shot.

Clicking the request token button should give you a token, be sure that it has been selected.
Now when you send the request again you will finnd that it works!





















http://localhost:8080/auth/realms/SpringBoot/.well-known/openid-configuration




#NOTES - this is really nice from the point of the minimal nature of the code but it is not a rest example - can this be altered to reflect rest?
