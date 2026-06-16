package pnb.service

class GlobalCheckService {

    static List extractIntegers(String input){

        return input.findAll( /\d+/ )*.toString()
    }

    static String removeSpecialCharacters(String input) {
        return input.replaceAll("[^A-Za-z0-9]+", "")
    }

}
