package pnb.service

import org.passay.CharacterSequence
import org.passay.EnglishSequenceData
import org.passay.IllegalSequenceRule
import org.passay.NumberRangeRule
import org.passay.PasswordData
import org.passay.PasswordValidator
import org.passay.RuleResult

import java.util.regex.Matcher
import java.util.regex.Pattern

class PolicyCheckService {

    //Should return true if violated

    static boolean checkBirthDateAsPartOfString(String input, Date birthDate){
        String yearFull = birthDate.format('yyyy')
        String year = birthDate.format('yy')
        String month = birthDate.format('mm')
        String day = birthDate.format('dd')

//        if(input.contains(yearFull)) return true
//        if(input.contains(year)) return true
//        if(input.contains(month)) return true
//        if(input.contains(day)) return true

        //need to return something or else groovy will return last object which is always true
        return false
    }

    static boolean checkThreeTimesCharacter(String input){
        Pattern pattern = Pattern.compile('^.*(.)\\1\\1.*$')
        Matcher matcher;
        matcher = pattern.matcher(input.toUpperCase());
        return matcher.find()
    }

    static boolean checkPasswordContainsUsernameOrSecurePhrase(String input, String username, String securephrase){
        if(input.toUpperCase().contains(username.toUpperCase())) return true
        if(input.toUpperCase().contains(securephrase.toUpperCase())) return true

        //need to return something or else groovy will return last object which is always true
        return false
    }

    static boolean checkPasswordContainsUsernameOrSecureNumbers(String input, String username, String securephrase){
        boolean toReturn = false
        if(username) {
            List intUsername = GlobalCheckService.extractIntegers(username)

            intUsername.each {
                if (input.contains(it)) toReturn = true
            }
        }

        if(securephrase){
            List intSecurephrase = GlobalCheckService.extractIntegers(securephrase)

            intSecurephrase.each {
                if(input.contains(it)) toReturn = true
            }
        }

        //need to return something or else groovy will return last object which is always true
        return toReturn
    }

    static boolean checkPasswordContainsUsernameOrSecureNumbers(String username, String securephrase){
        boolean toReturn = false
        if(username) {
            List intUsername = GlobalCheckService.extractIntegers(username)

            intUsername.each {
                if (securephrase.contains(it)) toReturn = true
            }
        }

        if(securephrase){
            List intSecurephrase = GlobalCheckService.extractIntegers(securephrase)

            intSecurephrase.each {
                if(username.contains(it)) toReturn = true
            }
        }

        //need to return something or else groovy will return last object which is always true
        return toReturn
    }

    static boolean checkPasswordContainsUsernameOrSecureText(String input, String username, String securephrase){
        if(username) {
            username = username.replaceAll("[^a-zA-Z ]+","")
            if(input.toUpperCase().contains(username.toUpperCase())) return true
        }

        if(securephrase) {
            //remove special characters & numbers
            securephrase = securephrase.replaceAll("[^a-zA-Z ]+","")
            if(input.toUpperCase().contains(securephrase.toUpperCase())) return true
        }

        //need to return something or else groovy will return last object which is always true
        return false
    }

    static boolean checkPasswordContainsUsernameOrSecureText(String username, String securephrase){
        if(username) {
            username = username.replaceAll("[^a-zA-Z ]+","")
            if(securephrase.toUpperCase().contains(username.toUpperCase())) return true
        }

        if(securephrase) {
            //remove special characters & numbers
            securephrase = securephrase.replaceAll("[^a-zA-Z ]+","")
            if(username.toUpperCase().contains(securephrase.toUpperCase())) return true
        }

        //need to return something or else groovy will return last object which is always true
        return false
    }

    static boolean checkFullNameInsideCredentials(String input, String fullName){
        boolean toReturn = false
        if(fullName && input){
            List nameChunks = fullName.toUpperCase().tokenize(' ')
            nameChunks.each {
                if(input.toUpperCase().contains(it) && it.length() > 1) toReturn = true
            }

            //need to return something or else groovy will return last object which is always true
            return toReturn
        }
    }

    static boolean checkForSpace(String input){
        boolean toReturn = false
        if(input){
            if (input.toString().contains(" ")) toReturn = true

            return toReturn
        }
    }

    static boolean checkMinimumMaximumLength(String input){
        if(input.length() < 8){
            return true
        }

        if(input.length() > 20){
            return true
        }
    }

    static boolean checkMinimumMaximumLengthUsername(String input){
        if(input.length() < 6){
            return true
        }

        if(input.length() > 16){
            return true
        }
    }

    static boolean checkMinimumMaximumLengthSP(String input){
        if(input.length() < 6){
            return true
        }

        if(input.length() > 40){
            return true
        }
    }

    static boolean checkCombinationOfSmallCapitalNumbers(String input){
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < input.length();i++) {
            ch = input.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag)
                return false;
        }
        return true;
    }

    static boolean checkCombinationOfSmallCapitalNumbersUsername(String input){
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < input.length();i++) {
            ch = input.charAt(i);
            /*if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }*/
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
        }

        //if(numberFlag && capitalFlag && lowerCaseFlag) return false;
        if(numberFlag && lowerCaseFlag) return false;

        return true;
    }

    static boolean checkCombinationOfSmallCapitalNumbersSP(String input){
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < input.length();i++) {
            ch = input.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag)
                return false;
        }
        return true;
    }

    static boolean checkSpeciaCharactersIsAllowed(String input){
        //String specialCharsRemoved = GlobalCheckService.removeSpecialCharacters(input)
        String specialCharsOnly = input.replaceAll("[!A-Za-z0-9]+", "")
        if(specialCharsOnly){
            char ch;
            boolean speciaCharsFlag = false
            for(int i=0;i < specialCharsOnly.length();i++) {
                ch = specialCharsOnly.charAt(i);
                if(!['@','!', '$', '%', '*'].contains(ch.toString())) {
                    speciaCharsFlag = true;
                }
            }
            if(speciaCharsFlag){
                return true
            }
        }
        return false
    }

    static boolean checkSecurePhraseWithUsername(String username, String securePhrase)
    {
        if(username && securePhrase)
        {
            securePhrase = takeFront(securePhrase)

            Pattern pattern = Pattern.compile('^'+securePhrase)
            Matcher matcher

            matcher = pattern.matcher(username)
            return matcher.find()
        }

        return false

    }

    static boolean checkPasswordWithSecurePhrase(String securePhrase, String password)
    {

        password = takeFront(password)

        if(password.contains("**"))
        {
            if(securePhrase.contains(password))
            {
                return true
            }
        }

        if(!password.contains("**"))
        {
            if(securePhrase && password)
            {
                Pattern pattern = Pattern.compile('^'+password)
                Matcher matcher

                matcher = pattern.matcher(securePhrase)
                return matcher.find()
            }
        }

        return false
    }

    static boolean checkPasswordWithUsername(String username, String password)
    {

        password = takeFront(password)

        if(password.contains("**"))
        {
            if(username.contains(password))
            {
                return true
            }
        }

        if(!password.contains("**"))
        {
            if(username && password)
            {
                Pattern pattern = Pattern.compile('^'+password)
                Matcher matcher

                matcher = pattern.matcher(username)
                return matcher.find()
            }
        }

        return false
    }

    static String takeFront(String input)
    {
        def lengthInput = input.length()
        def calcLength = lengthInput/2
        int finalLength

        if((calcLength-(int)calcLength)!=0)
        {
            // decimal value is there
            finalLength = Math.round(calcLength)
        }
        else
        {
            // decimal value is not there
            finalLength = calcLength + 1
        }

        return input.take(finalLength)
    }

    static boolean CheckSequenceAlphabetical(String input)
    {
        //NumberRangeRule numberRangeRule = new NumberRangeRule(0, 9)
        //CharacterSequence sequenceRule = new CharacterSequence("0123456789")
        // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abc)
        IllegalSequenceRule illegalSequenceRule = new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false)

        PasswordValidator validator = new PasswordValidator(illegalSequenceRule);
        PasswordData password = new PasswordData(input);
        RuleResult result = validator.validate(password)

        return result.isValid()
    }

    static boolean CheckSequenceNumerical(String input)
    {
        //NumberRangeRule numberRangeRule = new NumberRangeRule(0, 9)
        //CharacterSequence sequenceRule = new CharacterSequence("0123456789")
        // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 123)
        IllegalSequenceRule illegalSequenceRule = new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false)

        PasswordValidator validator = new PasswordValidator(illegalSequenceRule);
        PasswordData password = new PasswordData(input);
        RuleResult result = validator.validate(password)

        return result.isValid()
    }

    static boolean CheckSpecialCharacter(String input)
    {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return m.find()
    }
}
