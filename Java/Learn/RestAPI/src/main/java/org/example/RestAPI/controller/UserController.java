package org.example.RestAPI.controller;

import org.example.RestAPI.exporter.UserExcelExporter;
import org.example.RestAPI.finalstring.FinalMessage;
import org.example.RestAPI.importer.UserExcelImporter;
import org.example.RestAPI.model.Message;
import org.example.RestAPI.model.User;
import org.example.RestAPI.request.user.ChangePasswordRequest;
import org.example.RestAPI.request.user.LoginRequest;
import org.example.RestAPI.request.user.SignupRequest;
import org.example.RestAPI.response.importer.UserExcelImporterResponse;
import org.example.RestAPI.response.user.ChangePasswordResponse;
import org.example.RestAPI.response.user.LoginResponse;
import org.example.RestAPI.response.user.SignupResponse;
import org.example.RestAPI.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;

    @GetMapping("/all")
    public List<User> findAll(){
        return userService.findAll();
    }

    @GetMapping("/")
    public String rootPage(){
        return "rootpage";
    }

    @PostMapping("/signup")
    public ResponseEntity addUser(@RequestBody SignupRequest request){
//        System.out.println(request.getResult());
        Optional<User> findUser = userService.findByUser_name(request.getUser_name());
        Message<SignupResponse> message;
        if (findUser.isEmpty()){
            if (request.getResult().equals("OK")){
                User user = new User(request.getUser_name(), request.getPassword());
                userService.addUser(user);
                message = new Message<>(FinalMessage.SIGNUP_SUCCESS, new SignupResponse(user.getId()));
            }
            else{
                message = new Message<>(request.getResult(), null);
            }
        }
        else{
            message = new Message<>(FinalMessage.USERNAME_EXISTED, null);
        }
        return new ResponseEntity<Message<SignupResponse>>(message, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request){
//        System.out.println(user.getUser_name());
        Optional<User> findUser = userService.findByUser_name(request.getUser_name());
        Message<LoginResponse> message;
        if (findUser.isEmpty()){
            message = new Message<>(FinalMessage.NO_USER, null);
        }
        else{
            if (!request.getPassword().equals(findUser.get().getPassword())){
                message = new Message<>(FinalMessage.WRONG_PASSWORD, null);
            }
            else{
                message = new Message<>(FinalMessage.LOGIN_SUCCESS,
                        new LoginResponse(findUser.get()));
            }
        }
        return new ResponseEntity<Message<LoginResponse>>(message, HttpStatus.OK);
    }

    @PutMapping("/changepassword")
    public ResponseEntity changePassword(@RequestBody ChangePasswordRequest request){
        Optional<User> findUser = userService.findById(request.getId());
        Message<ChangePasswordResponse> message;
        if (findUser.isEmpty()){
            message = new Message<>(FinalMessage.NO_USER, null);
        }
        else{
            if (!request.getOld_password().equals(findUser.get().getPassword())){
                message = new Message<>(FinalMessage.WRONG_PASSWORD, null);
            }
            else{
                if (!request.getResult().equals("OK")){
                    message = new Message<>(request.getResult(), null);
                }
                else{
                    User user = findUser.get();
                    user.setPassword(request.getNew_password());
                    userService.addUser(user);
                    message = new Message<>(FinalMessage.CHANGE_PASSWORD_SUCCESS,
                            new ChangePasswordResponse(request.getId()));
                }
            }
        }
        return new ResponseEntity<Message<ChangePasswordResponse>>(message, HttpStatus.OK);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<Resource> getFile(){
        String filename = "user.xlsx";
        InputStreamResource file = new InputStreamResource(userService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/import/excel")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {

        if (UserExcelImporter.hasExcelFormat(file)) {
            try {
                userService.save(file);
                return ResponseEntity.status(HttpStatus.OK).body(new UserExcelImporterResponse(FinalMessage.IMPORT_EXCEL_FILE_SUCCESS));

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new UserExcelImporterResponse(FinalMessage.IMPORT_EXCEL_FILE_FAIL));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserExcelImporterResponse(FinalMessage.NOT_EXCEL_FILE));
    }

    @GetMapping("/getalluser")
    public ResponseEntity<List<User>> getAllTutorials() {
        try {
            List<User> listUser = userService.findAll();

            if (listUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(listUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
