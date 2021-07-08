/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.1.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package fr.finanting.server.generated.api;

import fr.finanting.server.generated.model.PasswordUpdateParameter;
import fr.finanting.server.generated.model.UserDTO;
import fr.finanting.server.generated.model.UserRegistrationParameter;
import fr.finanting.server.generated.model.UserUpdateParameter;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-08T10:10:39.885064+02:00[Europe/Paris]")
@Validated
@Api(value = "user", description = "the user API")
public interface UserApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /user : Get account informations
     *
     * @return successful operation (status code 200)
     */
    @ApiOperation(value = "Get account informations", nickname = "userGet", notes = "", response = UserDTO.class, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = UserDTO.class) })
    @GetMapping(
        value = "/user",
        produces = { "application/json" }
    )
    default ResponseEntity<UserDTO> userGet() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"roles\" : [ \"ADMIN\", \"ADMIN\" ], \"userName\" : \"userName\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /user/updatePassword : Update the password of the user
     *
     * @param passwordUpdateParameter Object that needs to be send to update a user password (optional)
     * @return successful operation (status code 200)
     */
    @ApiOperation(value = "Update the password of the user", nickname = "userPasswordUpdate", notes = "", tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation") })
    @PostMapping(
        value = "/user/updatePassword",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> userPasswordUpdate(@ApiParam(value = "Object that needs to be send to update a user password"  )  @Valid @RequestBody(required = false) PasswordUpdateParameter passwordUpdateParameter) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /user/registration : Register a new user account
     *
     * @param userRegistrationParameter Object that needs to be send to register a new user (optional)
     * @return successful operation (status code 201)
     */
    @ApiOperation(value = "Register a new user account", nickname = "userRegistration", notes = "", response = UserDTO.class, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "successful operation", response = UserDTO.class) })
    @PutMapping(
        value = "/user/registration",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<UserDTO> userRegistration(@ApiParam(value = "Object that needs to be send to register a new user"  )  @Valid @RequestBody(required = false) UserRegistrationParameter userRegistrationParameter) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"roles\" : [ \"ADMIN\", \"ADMIN\" ], \"userName\" : \"userName\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /user : Update account informations
     *
     * @param userUpdateParameter Object that needs to be send to update a user (optional)
     * @return successful operation (status code 200)
     */
    @ApiOperation(value = "Update account informations", nickname = "userUpdate", notes = "", response = UserDTO.class, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = UserDTO.class) })
    @PostMapping(
        value = "/user",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<UserDTO> userUpdate(@ApiParam(value = "Object that needs to be send to update a user"  )  @Valid @RequestBody(required = false) UserUpdateParameter userUpdateParameter) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"roles\" : [ \"ADMIN\", \"ADMIN\" ], \"userName\" : \"userName\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
