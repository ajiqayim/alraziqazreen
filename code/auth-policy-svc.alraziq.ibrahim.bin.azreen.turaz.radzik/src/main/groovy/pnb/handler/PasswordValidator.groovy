package pnb.handler

import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import pnb.service.I18n
import pnb.service.LogService
import pnb.service.PolicyCheckService

class PasswordValidator {
    /*
        For non registered member of myASNB portal
     */
    static Handler credentialMembersCheck = { RoutingContext routingContext ->
        String p_password = routingContext.request().getParam('password')
        String p_username = routingContext.request().getParam('username')
        String p_securephrase = routingContext.request().getParam('securephrase')
        String p_birthdate = routingContext.request().getParam('birthdate')
        String p_fullname = routingContext.request().getParam('fullname')
        String p_lang = routingContext.request().getParam('lang')
        JsonObject configApp = routingContext.get('configApp')
        I18n i18n = new I18n(configApp)
        Map responseTemplate = [
                password_errors: [],
                system_errors: ""
        ]

        List password_errors = []

        String givenFullName = p_fullname
        Date givenDate = Date.parse('dd/mm/yyyy',p_birthdate)
        String givenUsername = p_username
        String givenSecurephrase = p_securephrase
        String givenPassword = p_password

            try{

                //Password Checks
                if(!PolicyCheckService.CheckSequenceAlphabetical(givenPassword)){
                    password_errors.add(i18n.message("128",p_lang))
                }

                if(!PolicyCheckService.CheckSequenceNumerical(givenPassword)){
                    password_errors.add(i18n.message("129",p_lang))
                }

                //if(PolicyCheckService.checkBirthDateAsPartOfString(givenPassword, givenDate)){
                //    password_errors.add(i18n.message("101",p_lang))
                //}

                if(PolicyCheckService.checkFullNameInsideCredentials(givenPassword, givenFullName)){
                    password_errors.add(i18n.message("102",p_lang))
                }

                /*if(PolicyCheckService.checkPasswordContainsUsernameOrSecureNumbers(givenPassword, givenUsername, givenSecurephrase)){
                    password_errors.add(i18n.message("103",p_lang))
                }*/

                /*if(PolicyCheckService.checkPasswordContainsUsernameOrSecureText(givenPassword, givenUsername, givenSecurephrase)){
                    password_errors.add(i18n.message("104",p_lang))
                }*/

                /*if(PolicyCheckService.checkSecurePhraseWithUsername(givenUsername,givenSecurephrase)){
                    password_errors.add(i18n.message("125",p_lang))
                }*/

                if(PolicyCheckService.checkPasswordWithSecurePhrase(givenSecurephrase, givenPassword)){
                    password_errors.add(i18n.message("126",p_lang))
                }

                if(PolicyCheckService.checkPasswordWithUsername(givenUsername, givenPassword)){
                    password_errors.add(i18n.message("127",p_lang))
                }

                /*if(PolicyCheckService.checkThreeTimesCharacter(givenPassword)){
                    password_errors.add(i18n.message("105",p_lang))
                }*/

                if(PolicyCheckService.checkMinimumMaximumLength(givenPassword)){
                    password_errors.add(i18n.message("106",p_lang))
                }

                if(PolicyCheckService.checkCombinationOfSmallCapitalNumbers(givenPassword)){
                    password_errors.add(i18n.message("107",p_lang))
                }

                if(PolicyCheckService.checkSpeciaCharactersIsAllowed(givenPassword)){
                    password_errors.add(i18n.message("108",p_lang))
                }

                if(PolicyCheckService.checkForSpace(givenPassword)){
                    password_errors.add(i18n.message("124",p_lang))
                }

                if(!PolicyCheckService.CheckSpecialCharacter(givenPassword)){
                    password_errors.add(i18n.message("108",p_lang))
                }

                responseTemplate.password_errors = password_errors

                routingContext.response().putHeader('Content-Type', "application/json")
                        .setStatusCode(200).end(new JsonObject(responseTemplate).encodePrettily())

                LogService.log('credentialMembersCheck', new JsonObject(responseTemplate), routingContext)

                LogService.logServiceFlushHandler(routingContext)
            }catch(e){

                responseTemplate.system_errors = e.message

                routingContext.response().putHeader('Content-Type', "application/json")
                        .setStatusCode(500).end(new JsonObject(responseTemplate).encodePrettily())

                LogService.log('credentialMembersCheck', new JsonObject(responseTemplate), routingContext)

                LogService.logServiceFlushHandler(routingContext)
            }

    }

    static Handler preCredentialNonMembersCheck = { RoutingContext routingContext ->
        String p_username = routingContext.request().getParam('username')
        String p_securephrase = routingContext.request().getParam('securephrase')
        String p_birthdate = routingContext.request().getParam('birthdate')
        String p_fullname = routingContext.request().getParam('fullname')
        String p_lang = routingContext.request().getParam('lang')
        JsonObject configApp = routingContext.get('configApp')
        I18n i18n = new I18n(configApp)
        String p_password
        String givenPassword
        boolean containsPassword = false
        Map responseTemplate = [
                password_errors: [],
                system_errors: ""
        ]

        List password_errors = []

        if(routingContext.request().params().contains("password")){
            containsPassword = true
            p_password = routingContext.request().getParam('password')
            givenPassword = p_password
        }

        String givenFullName = p_fullname
        Date givenDate = Date.parse('dd/MM/yyyy',p_birthdate)
        String givenUsername = p_username
        String givenSecurephrase = p_securephrase

        try {
            if(containsPassword){
            //Password Checks
                if(!PolicyCheckService.CheckSequenceAlphabetical(givenPassword)){
                    password_errors.add(i18n.message("128",p_lang))
                }

                if(!PolicyCheckService.CheckSequenceNumerical(givenPassword)){
                    password_errors.add(i18n.message("129",p_lang))
                }

                //if (PolicyCheckService.checkBirthDateAsPartOfString(givenPassword, givenDate)) {
                //    password_errors.add(i18n.message("101", p_lang))
                //}

                if (PolicyCheckService.checkFullNameInsideCredentials(givenPassword, givenFullName)) {
                    password_errors.add(i18n.message("102", p_lang))
                }

                /*if (PolicyCheckService.checkPasswordContainsUsernameOrSecureNumbers(givenPassword, givenUsername, givenSecurephrase)) {
                    password_errors.add(i18n.message("103", p_lang))
                }*/

                /*if (PolicyCheckService.checkPasswordContainsUsernameOrSecureText(givenPassword, givenUsername, givenSecurephrase)) {
                    password_errors.add(i18n.message("104", p_lang))
                }*/
                /*if(PolicyCheckService.checkSecurePhraseWithUsername(givenUsername,givenSecurephrase)){
                    password_errors.add(i18n.message("125",p_lang))
                }*/

                if(PolicyCheckService.checkPasswordWithSecurePhrase(givenSecurephrase, givenPassword)){
                    password_errors.add(i18n.message("126",p_lang))
                }

                if(PolicyCheckService.checkPasswordWithUsername(givenUsername, givenPassword)){
                    password_errors.add(i18n.message("127",p_lang))
                }

                /*if (PolicyCheckService.checkThreeTimesCharacter(givenPassword)) {
                    password_errors.add(i18n.message("105", p_lang))
                }*/

                if (PolicyCheckService.checkMinimumMaximumLength(givenPassword)) {
                    password_errors.add(i18n.message("106", p_lang))
                }

                if (PolicyCheckService.checkCombinationOfSmallCapitalNumbers(givenPassword)) {
                    password_errors.add(i18n.message("107", p_lang))
                }

                if (PolicyCheckService.checkSpeciaCharactersIsAllowed(givenPassword)) {
                    password_errors.add(i18n.message("108", p_lang))
                }

                if(PolicyCheckService.checkForSpace(givenPassword)){
                    password_errors.add(i18n.message("124",p_lang))
                }

                if(!PolicyCheckService.CheckSpecialCharacter(givenPassword)){
                    password_errors.add(i18n.message("108",p_lang))
                }
            }

            //Username Checks
            if(PolicyCheckService.checkMinimumMaximumLengthUsername(givenUsername)){
                password_errors.add(i18n.message("109",p_lang))
            }

            if(PolicyCheckService.checkCombinationOfSmallCapitalNumbersUsername(givenUsername)){
                password_errors.add(i18n.message("110",p_lang))
            }

            if(PolicyCheckService.checkThreeTimesCharacter(givenUsername)){
                password_errors.add(i18n.message("113",p_lang))
            }

            if(PolicyCheckService.checkBirthDateAsPartOfString(givenUsername, givenDate)){
                password_errors.add(i18n.message("115",p_lang))
            }

            /*if(PolicyCheckService.checkFullNameInsideCredentials(givenUsername, givenFullName)){
                password_errors.add(i18n.message("117",p_lang))
            }*/

            if(PolicyCheckService.checkForSpace(givenUsername)){
                password_errors.add(i18n.message("124",p_lang))
            }

            //Secure Phrase Checks
            if(PolicyCheckService.checkForSpace(givenSecurephrase)){
                password_errors.add(i18n.message("124",p_lang))
            }

            if(PolicyCheckService.checkMinimumMaximumLengthSP(givenSecurephrase)){
                password_errors.add(i18n.message("111",p_lang))
            }

            /*if(PolicyCheckService.checkCombinationOfSmallCapitalNumbersSP(givenSecurephrase)){
                password_errors.add(i18n.message("112",p_lang))
            }*/

            if(PolicyCheckService.checkThreeTimesCharacter(givenSecurephrase)){
                password_errors.add(i18n.message("114",p_lang))
            }

            if(PolicyCheckService.checkBirthDateAsPartOfString(givenSecurephrase, givenDate)){
                password_errors.add(i18n.message("116",p_lang))
            }

            /*if(PolicyCheckService.checkFullNameInsideCredentials(givenSecurephrase, givenFullName)){
                password_errors.add(i18n.message("118",p_lang))
            }*/

            /*if(PolicyCheckService.checkSecurePhraseWithUsername(givenUsername,givenSecurephrase)){
                password_errors.add(i18n.message("125",p_lang))
            }*/


            responseTemplate.password_errors = password_errors

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(200).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('preCredentialNonMembersCheck', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }catch(e){

            responseTemplate.system_errors = e.message

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(500).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('preCredentialNonMembersCheck', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }
    }

    static Handler preCredentialNonMembersCheckUsername = { RoutingContext routingContext ->
        String p_username = routingContext.request().getParam('username')
        String p_lang = routingContext.request().getParam('lang')
        JsonObject configApp = routingContext.get('configApp')
        I18n i18n = new I18n(configApp)

        Map responseTemplate = [
                username_errors: [],
                system_errors: ""
        ]

        List username_errors = []

        String givenUsername = p_username

        try {

            //Username Checks
            if(PolicyCheckService.checkMinimumMaximumLengthUsername(givenUsername)){
                username_errors.add(i18n.message("109",p_lang))
            }

            if(PolicyCheckService.checkCombinationOfSmallCapitalNumbersUsername(givenUsername)){
                username_errors.add(i18n.message("110",p_lang))
            }

            if(PolicyCheckService.checkThreeTimesCharacter(givenUsername)){
                username_errors.add(i18n.message("113",p_lang))
            }

            if(PolicyCheckService.checkForSpace(givenUsername)){
                username_errors.add(i18n.message("124",p_lang))
            }

            responseTemplate.username_errors = username_errors

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(200).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('preCredentialNonMembersCheckUsername', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }catch(e){

            responseTemplate.system_errors = e.message

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(500).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('preCredentialNonMembersCheckUsername', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }
    }

    static Handler preCredentialNonMembersCheckPassword = { RoutingContext routingContext ->
        String p_username = routingContext.request().getParam('username')
        String p_lang = routingContext.request().getParam('lang')
        JsonObject configApp = routingContext.get('configApp')
        I18n i18n = new I18n(configApp)
        String p_password
        String givenPassword

        boolean containsPassword = false
        Map responseTemplate = [
                password_errors: [],
                system_errors: ""
        ]

        List password_errors = []

        if(routingContext.request().params().contains("password")){
            containsPassword = true
            p_password = routingContext.request().getParam('password')
            givenPassword = p_password
        }

        String givenUsername = p_username

        try {
            if(containsPassword){
                //Password Checks
                if(!PolicyCheckService.CheckSequenceAlphabetical(givenPassword)){
                    password_errors.add(i18n.message("128",p_lang))
                }

                if(!PolicyCheckService.CheckSequenceNumerical(givenPassword)){
                    password_errors.add(i18n.message("129",p_lang))
                }

                if(PolicyCheckService.checkPasswordWithUsername(givenUsername, givenPassword)){
                    password_errors.add(i18n.message("127",p_lang))
                }

                if (PolicyCheckService.checkMinimumMaximumLength(givenPassword)) {
                    password_errors.add(i18n.message("106", p_lang))
                }

                if (PolicyCheckService.checkCombinationOfSmallCapitalNumbers(givenPassword)) {
                    password_errors.add(i18n.message("107", p_lang))
                }

                if (PolicyCheckService.checkSpeciaCharactersIsAllowed(givenPassword)) {
                    password_errors.add(i18n.message("108", p_lang))
                }

                if(PolicyCheckService.checkForSpace(givenPassword)){
                    password_errors.add(i18n.message("124",p_lang))
                }

                if(!PolicyCheckService.CheckSpecialCharacter(givenPassword)){
                    password_errors.add(i18n.message("108",p_lang))
                }
            }

            responseTemplate.password_errors = password_errors

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(200).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('preCredentialNonMembersCheckPassword', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }catch(e){

            responseTemplate.system_errors = e.message

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(500).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('preCredentialNonMembersCheckPassword', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }
    }

    static Handler securePhraseMembersCheck = { RoutingContext routingContext ->
        String p_username = routingContext.request().getParam('username')
        String p_password = routingContext.request().getParam('password')
        String p_securephrase = routingContext.request().getParam('securephrase')
        String p_birthdate = routingContext.request().getParam('birthdate')
        String p_fullname = routingContext.request().getParam('fullname')
        String p_lang = routingContext.request().getParam('lang')
        JsonObject configApp = routingContext.get('configApp')
        I18n i18n = new I18n(configApp)
        Map responseTemplate = [
                password_errors: [],
                system_errors: ""
        ]

        List password_errors = []

        String givenFullName = p_fullname
        Date givenDate = Date.parse('dd/mm/yyyy',p_birthdate)
        String givenSecurephrase = p_securephrase
        String givenUsername = p_username

        try{

            /*if(PolicyCheckService.checkPasswordContainsUsernameOrSecureNumbers(givenUsername, givenSecurephrase)){
                password_errors.add(i18n.message("119",p_lang))
            }*/

            /*if(PolicyCheckService.checkPasswordContainsUsernameOrSecureText(givenUsername, givenSecurephrase)){
                password_errors.add(i18n.message("120",p_lang))
            }*/

            /*if(PolicyCheckService.checkPasswordContainsUsernameOrSecureNumbers(p_password, givenSecurephrase)){
                password_errors.add(i18n.message("121",p_lang))
            }*/

            /*if(PolicyCheckService.checkPasswordContainsUsernameOrSecureText(p_password, givenSecurephrase)){
                password_errors.add(i18n.message("122",p_lang))
            }*/
            /*if(PolicyCheckService.checkSecurePhraseWithUsername(givenUsername,givenSecurephrase)){
                password_errors.add(i18n.message("125",p_lang))
            }*/

            if(PolicyCheckService.checkPasswordWithSecurePhrase(givenSecurephrase, p_password)){
                password_errors.add(i18n.message("126",p_lang))
            }

            if(PolicyCheckService.checkPasswordWithUsername(givenUsername, p_password)){
                password_errors.add(i18n.message("127",p_lang))
            }

            //Secure Phrase Checks
            if(PolicyCheckService.checkMinimumMaximumLengthSP(givenSecurephrase)){
                password_errors.add(i18n.message("111",p_lang))
            }

            /*if(PolicyCheckService.checkCombinationOfSmallCapitalNumbersSP(givenSecurephrase)){
                password_errors.add(i18n.message("112",p_lang))
            }*/
            if(PolicyCheckService.checkForSpace(givenSecurephrase)){
                password_errors.add(i18n.message("124",p_lang))
            }

            if(PolicyCheckService.checkThreeTimesCharacter(givenSecurephrase)){
                password_errors.add(i18n.message("114",p_lang))
            }

            if(PolicyCheckService.checkBirthDateAsPartOfString(givenSecurephrase, givenDate)){
                password_errors.add(i18n.message("116",p_lang))
            }

            /*if(PolicyCheckService.checkFullNameInsideCredentials(givenSecurephrase, givenFullName)){
                password_errors.add(i18n.message("118",p_lang))
            }*/

            responseTemplate.password_errors = password_errors

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(200).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('securePhraseMembersCheck', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)

        }catch(e){

            responseTemplate.system_errors = e.message

            routingContext.response().putHeader('Content-Type', "application/json")
                    .setStatusCode(500).end(new JsonObject(responseTemplate).encodePrettily())

            LogService.log('securePhraseMembersCheck', new JsonObject(responseTemplate), routingContext)

            LogService.logServiceFlushHandler(routingContext)
        }
    }
}
