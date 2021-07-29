package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.ExerciceForm;
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
import java.util.Arrays;
import java.util.List;

@Controller
public class ExerciseController {

    @Value("${api.levelUp.url}")
    private String apiLevelUpUrl;

    @Value("${error.empty.code.input}")
    private String errorCodeEmpty;

    @RequestMapping(value = { "/exerciceList" }, method = RequestMethod.GET)
    public String showExerciceListPage(Model model, HttpServletRequest request, HttpSession session){

        List<Exercice> exercices;
        ExerciceForm exerciceForm = new ExerciceForm();
        try{
            HttpResponse<String> response = MainController.doGetWithAccessToken(apiLevelUpUrl + "exercises/all",session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            exercices = Arrays.asList(mapper.readValue(response.body(), Exercice[].class));
        }catch (Exception e){
            return "error";
        }
        model.addAttribute("exercices", exercices);
        model.addAttribute("exerciceForm",exerciceForm);

        return "exerciceList";
    }


    @RequestMapping(value = { "/exercice" }, params = { "id" } , method = RequestMethod.GET)
    public String showExercicePage(Model model, HttpSession session , @RequestParam("id") Integer id){

        ExerciceForm exerciceForm = new ExerciceForm();

        User user = (User) session.getAttribute("user");
        try {
            HttpResponse<String> response = MainController.doGetWithAccessToken(apiLevelUpUrl + "exercises/" + id , session.getAttribute("accessToken").toString());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Exercice exercice = mapper.readValue(response.body(), Exercice.class);
            exerciceForm.setExerciceId(id);
            exerciceForm.setTitle(exercice.getTitle());
            exerciceForm.setStatement(exercice.getContent());

            response = MainController.doGetWithAccessToken(apiLevelUpUrl + "responses/?user_id="+user.getId()+"&exercise_id=" + id , session.getAttribute("accessToken").toString());
            ResponseApi resp = mapper.readValue(response.body(), ResponseApi.class);
            if(resp != null){

                exerciceForm.setCode(resp.getCodeSent());
                model.addAttribute("status",resp.getStatus());

                List<Comment> comments;
                response = MainController.doGetWithAccessToken(apiLevelUpUrl + "comment/response/"+resp.getId() , session.getAttribute("accessToken").toString());
                System.out.println("Response test : " + response);
                comments = Arrays.asList(mapper.readValue(response.body(), Comment[].class));
                System.out.println("comment test : " + comments.toString()) ;

                model.addAttribute("comments",comments);
            }
        }catch (Exception e){
            return "error";
        }

        model.addAttribute("exerciceForm", exerciceForm);
        return "exercice";
    }


    @RequestMapping(value = { "/exercice" }, method = RequestMethod.POST)
    public String postExercice(Model model, HttpSession session ,
                               @ModelAttribute("exerciceForm") ExerciceForm exerciceForm) throws Exception {


        if (exerciceForm.getCode() != null && exerciceForm.getCode().length() > 0) {
            try {
                User user = (User) session.getAttribute("user");
                Response response = new Response(
                        user.getId().toString(),
                        exerciceForm.getExerciceId().toString(),
                        exerciceForm.getCode()
                );
                System.out.println("Langage : " + exerciceForm.getLangage());
                System.out.println("Vérification send Exercice , user id : " + response.getUserid() + " exercice_id : " + response.getExerciseid() + " code_sent : " + response.getCodeSent());
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(response);
                String responseHttp = MainController.doPostWithAccessToken(apiLevelUpUrl + "responses/send-" + exerciceForm.getLangage(),json,session.getAttribute("accessToken").toString());

                model.addAttribute("status", responseHttp);
                return "redirect:/exercice?id="+exerciceForm.getExerciceId();

            }catch (Exception e){
                return "error";
            }
        }else{
            model.addAttribute("errorCodeEmpty", errorCodeEmpty);
            return "redirect:/exercice?id="+exerciceForm.getExerciceId();
        }
    }



}
