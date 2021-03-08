package org.example.RestAPI.controller;

import org.example.RestAPI.finalstring.FinalMessage;
import org.example.RestAPI.model.Message;
import org.example.RestAPI.model.User;
import org.example.RestAPI.model.Wallet;
import org.example.RestAPI.request.wallet.CreateWalletRequest;
import org.example.RestAPI.request.wallet.DeleteWalletRequest;
import org.example.RestAPI.request.wallet.UpdateWalletRequest;
import org.example.RestAPI.response.wallet.CreateWalletResponse;
import org.example.RestAPI.response.wallet.DeleteWalletResponse;
import org.example.RestAPI.response.wallet.GetListWalletResponse;
import org.example.RestAPI.response.wallet.UpdateWalletResponse;
import org.example.RestAPI.service.IUserService;
import org.example.RestAPI.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    IUserService userService;

    @Autowired
    IWalletService walletService;

    @PostMapping("/create")
    public ResponseEntity createWallet(@RequestBody CreateWalletRequest request){
        Optional<User> findUser = userService.findById(request.getUser_id());
        Message<CreateWalletResponse> message;
        if (findUser.isEmpty()){
            message = new Message<>(FinalMessage.NO_USER, null);
        }
        else{
            if (!request.getResult().equals("OK")){
                message = new Message<>(request.getResult(), null);
            }
            else{
                Wallet wallet = new Wallet();
                wallet.setWallet_name(request.getWallet_name());

                findUser.get().addWallet(wallet);

                walletService.addWallet(wallet);

                message = new Message<>(FinalMessage.CREATE_WALLET_SUCCESS,
                        new CreateWalletResponse(wallet.getId()));
            }
        }
        return new ResponseEntity<Message<CreateWalletResponse>>(message, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity updateWallet(@RequestBody UpdateWalletRequest request){
        Optional<Wallet> findWallet = walletService.findById(request.getWallet_id());
        Message<UpdateWalletResponse> message;

        if (findWallet.isEmpty()){
            message = new Message<>(FinalMessage.NO_WALLET, null);
        }
        else{
            if (!request.getResult().equals("OK")){
                message = new Message<>(request.getResult(), null);
            }
            else{
                Wallet wallet = findWallet.get();
                wallet.setWallet_name(request.getWallet_name());
                walletService.addWallet(wallet);

                message = new Message<>(FinalMessage.CHANGE_WALLET_NAME_SUCCESS,
                        new UpdateWalletResponse(wallet.getId(), wallet.getWallet_name(),
                                wallet.getCreated_date(), wallet.getModified_date(), wallet.getTotal_amount()));
            }
        }
        return new ResponseEntity<Message<UpdateWalletResponse>>(message, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity getListWallet(@RequestParam(name = "userId") long user_id){
        Optional<User> findUser = userService.findById(user_id);
        Message<GetListWalletResponse> message;

        if (findUser.isEmpty()){
            message = new Message<>(FinalMessage.NO_USER, null);
        }
        else{
            GetListWalletResponse response = new GetListWalletResponse(findUser.get());
            message = new Message<>(FinalMessage.GET_LIST_WALLET_SUCCESS, response);
        }
        return new ResponseEntity<Message<GetListWalletResponse>>(message, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity deleteWallet(@RequestBody DeleteWalletRequest request){
        Optional<Wallet> findWallet = walletService.findById(request.getWallet_id());
        Message<DeleteWalletResponse> message;

        if(findWallet.isEmpty()){
            message = new Message<>(FinalMessage.NO_WALLET, null);
        }
        else{
            walletService.deleteWallet(request.getWallet_id());

            message = new Message<>(FinalMessage.)
        }
    }
}
