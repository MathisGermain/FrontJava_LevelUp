package com.esgi.al2.application.java.levelUp.controller;

import com.esgi.al2.application.java.levelUp.form.ElementList;
import com.esgi.al2.application.java.levelUp.form.ExerciceForm;
import com.esgi.al2.application.java.levelUp.form.LoginForm;
import com.esgi.al2.application.java.levelUp.model.Exercice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    // Injectez (inject) via application.properties.
    @Value("${welcome.message}")
    private String message;

    @Value("${error.empty.login.input}")
    private String errorLoginEmpty;

    @Value("${error.login.invalid}")
    private String errorLoginInvalid;

    @Value("${error.empty.code.input}")
    private String errorCodeEmpty;





    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("message", message);

        return "index";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public String showLoginPage(Model model) {

        LoginForm loginForm = new LoginForm();
        model.addAttribute("loginForm", loginForm);

        return "login";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.POST)
    public String postLogin(Model model, //
    @ModelAttribute("loginForm") LoginForm loginForm) {

        String email = loginForm.getEmail();
        String password = loginForm.getPassword();
        System.out.println("Verification " + email);

        if (email != null && email.length() > 0 //
                && password != null && password.length() > 0) {

            // Appel API pour vérifier les informations du User
            if(1==1){
                // Ajout de l'id user en session
                System.out.println("Ok");
                return "redirect:/index";
            }else{
                model.addAttribute("errorLoginInvalid", errorLoginInvalid);
                return "login";
            }


        }else{
            model.addAttribute("errorLoginEmpty", errorLoginEmpty);
            return "login";
        }
    }

    @RequestMapping(value = { "/exerciceList" }, method = RequestMethod.GET)
    public String showExerciceListPage(Model model) {

        List<Exercice> exercices = new ArrayList<Exercice>();

        Exercice ex = new Exercice();
        ex.setId(1);
        ex.setTitle("Test");
        ex.setStatement("Lorem ipsum");

        exercices.add(ex);


        //appel api pour récupérer la liste d'exercice non résolu
        model.addAttribute("exercices", exercices);

        return "exerciceList";
    }

    @RequestMapping(value = { "/solvedExerciceList" }, method = RequestMethod.GET)
    public String showSolvedExerciceListPage(Model model) {

        List<Exercice> exercices = new ArrayList<Exercice>();

        Exercice ex = new Exercice();
        ex.setId(1);
        ex.setTitle("Test");
        ex.setStatement("Lorem ipsum");

        exercices.add(ex);
        exercices.add(ex);
        exercices.add(ex);
        exercices.add(ex);
        //appel api pour récupérer la liste d'exercice résolu


        model.addAttribute("solvedExercices", exercices);

        return "solvedExerciceList";
    }

    @RequestMapping(value = { "/getExercice" }, method = RequestMethod.POST)
    public String showExercicePage(Model model, //
                            @ModelAttribute("elementList") ElementList elementList){




        ExerciceForm exerciceForm = new ExerciceForm();
        exerciceForm.setExerciceId(elementList.getId());
        exerciceForm.setStatement(elementList.getStatement());
        exerciceForm.setTitle(elementList.getTitle());
        model.addAttribute("exerciceForm", exerciceForm);


        return "exercice";
    }

    @RequestMapping(value = { "/exercice" }, method = RequestMethod.POST)
    public String postExercice(Model model, //
                            @ModelAttribute("exerciceForm") ExerciceForm exerciceForm) {

        String code = exerciceForm.getCode();
        Integer exerciceId = exerciceForm.getExerciceId();

        if (code != null && code.length() > 0) {

            // Appel API pour envoyer le code Utilisateur

            System.out.println("Ok");


            return "redirect:/exercice";
        }else{
            model.addAttribute("errorCodeEmpty", errorCodeEmpty);
            return "exercice";
        }
    }

}