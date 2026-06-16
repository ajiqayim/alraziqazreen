package pnb.service

import io.vertx.core.json.JsonObject

class I18n {
    static JsonObject configApp

    I18n(JsonObject configAppObject) {
        configApp = configAppObject
    }

    static String message(String code, String lang){
        return configApp.getJsonObject('application').getJsonObject('i18n').getString(code+lang)
    }
}
