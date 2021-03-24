package org.example.RestAPI.controller;

import org.example.RestAPI.exporter.UserExcelExporter;
import org.example.RestAPI.finalstring.FinalMessage;
import org.example.RestAPI.model.Message;
import org.example.RestAPI.model.User;
import org.example.RestAPI.request.user.ChangePasswordRequest;
import org.example.RestAPI.request.user.LoginRequest;
import org.example.RestAPI.request.user.SignupRequest;
import org.example.RestAPI.response.user.ChangePasswordResponse;
import org.example.RestAPI.response.user.LoginResponse;
import org.example.RestAPI.response.user.SignupResponse;
import org.example.RestAPI.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("export/excel")
    public void exportExcelFile(HttpServletResponse response) throws Exception{
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = userService.findAll();

        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);

        excelExporter.export(response);
    }
}
