package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.ExerciceForm;
import com.esgi.al2.application.java.levelUp.form.ResponseForm;
import com.esgi.al2.application.java.levelUp.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ResponseController {

    @Value("${api.levelUp.url}")
    private String apiLevelUpUrl;

    @Value("${add.comment.success}")
    private String commentSuccess;

    @RequestMapping(value = { "/solvedExerciceList" }, method = RequestMethod.GET)
    public String showSolvedExerciceListPage(Model model, HttpServletRequest request, HttpSession session){

        List<Exercice> exercices;
        User sameObject = (User) session.getAttribute("user");
        ExerciceForm exerciceForm = new ExerciceForm();
        try{
            HttpResponse<String> response = MainController.doGetWithAccessToken(apiLevelUpUrl + "exercises/all",session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            exercices = Arrays.asList(mapper.readValue(response.body(), Exercice[].class));
        }catch (Exception e){
            return "error";
        }
        model.addAttribute("solvedExercices", exercices);

        return "solvedExerciceList";
    }

    @RequestMapping(value = { "/usersResponsesList" }, params = { "id" } , method = RequestMethod.GET)
    public String showUsersResponsesList(Model model, HttpSession session ,@RequestParam("id") Integer id){

        ExerciceForm exerciceForm = new ExerciceForm();
        List<ResponseApi> responses;
        try {
            HttpResponse<String> response = MainController.doGetWithAccessToken(apiLevelUpUrl + "responses/exercise/" + id, session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responses = Arrays.asList(mapper.readValue(response.body(), ResponseApi[].class));
        }catch (Exception e){
            return "error";
        }
        model.addAttribute("responsesList",responses);
        model.addAttribute("exerciceForm", exerciceForm);

        return "userResponsesList";
    }

    @RequestMapping(value = { "/solvedExercice" }, params = { "id" } , method = RequestMethod.GET)
    public String showCommentPage(Model model, HttpSession session , @RequestParam("id") Integer id){

        ResponseForm responseForm = new ResponseForm();
        try {
            HttpResponse<String> response = MainController.doGetWithAccessToken(apiLevelUpUrl + "responses/" + id , session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ResponseApi responseApi = mapper.readValue(response.body(), ResponseApi.class);
            responseForm.setId(responseApi.getId());
            responseForm.setExerciceId(responseApi.getExerciseid());
            responseForm.setCode(responseApi.getCodeSent());
            responseForm.setUserId(responseApi.getUserid());

            response = MainController.doGetWithAccessToken(apiLevelUpUrl + "exercises/" + responseApi.getExerciseid() , session.getAttribute("accessToken").toString());
            mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Exercice exercice = mapper.readValue(response.body(), Exercice.class);

            responseForm.setTitle(exercice.getTitle());
            responseForm.setStatement(exercice.getContent());

        }catch (Exception e){
            return "error";
        }

        model.addAttribute("responseForm", responseForm);
        return "solvedExercise";
    }

    @RequestMapping(value = { "/solvedExercice" }, method = RequestMethod.POST)
    public String postComment(Model model, HttpSession session ,
                               @ModelAttribute("responseForm") ResponseForm responseForm) throws Exception {


        if (responseForm.getComment() != null && responseForm.getComment().length() > 0) {
            try {
                User user = (User) session.getAttribute("user");
                System.out.println("Ok");
                System.out.println("UserId : "+ user.getId().toString());
                Comment comment = new Comment();
                comment.setResponseid(responseForm.getId());
                comment.setUserid(user.getId().toString());
                comment.setContent(responseForm.getComment());
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(comment);
                String responseHttp = MainController.doPostWithAccessToken(apiLevelUpUrl + "comment/add",json,session.getAttribute("accessToken").toString());
                System.out.println(commentSuccess);
                model.addAttribute("status", commentSuccess);

            }catch (Exception e){
                return "error";
            }
        }else{
            //model.addAttribute("errorCodeEmpty", errorCodeEmpty);
        }
        return "redirect:/solvedExercice?id="+responseForm.getId();
    }

    @RequestMapping(value = { "/solvedExerciceUser" } , method = RequestMethod.GET)
    public String showSolvedExercisePage(Model model, HttpSession session){

        List<Exercice> exercices = new ArrayList<Exercice>();
        List<ResponseApi> responses;
        ResponseForm responseForm = new ResponseForm();
        try {
            User user = (User) session.getAttribute("user");
            System.out.println("User id : " + user.getId());
            HttpResponse<String> response = MainController.doGetWithAccessToken(apiLevelUpUrl + "responses/user/" + user.getId() , session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responses = Arrays.asList(mapper.readValue(response.body(), ResponseApi[].class));
            System.out.println("Réponses : " + response.body().toString());
            for (ResponseApi resp : responses) {
                HttpResponse<String> responseExercise = MainController.doGetWithAccessToken(apiLevelUpUrl + "exercises/" + resp.getExerciseid() , session.getAttribute("accessToken").toString());
                Exercice exercice = mapper.readValue(responseExercise.body(), Exercice.class);
                System.out.println("Exo : " + exercice.getTitle());
                exercices.add(exercice);
                System.out.println("Add Exercise");
            }
            System.out.println("Exe : " + exercices.get(0).getTitle());
            model.addAttribute("exercices", exercices);

        }catch (Exception e){
            return "error";
        }

        return "exerciceList";
    }




}
