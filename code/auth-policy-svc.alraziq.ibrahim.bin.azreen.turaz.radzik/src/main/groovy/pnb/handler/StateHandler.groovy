package pnb.handler

import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

class StateHandler {

    static final List<Map> STATES_DATA = [
            [state: "Johor",            capital: "Johor Bahru"],
            [state: "Kedah",            capital: "Alor Setar"],
            [state: "Kelantan",         capital: "Kota Bharu"],
            [state: "Malacca",          capital: "Malacca City"],
            [state: "Negeri Sembilan",  capital: "Seremban"],
            [state: "Pahang",           capital: "Kuantan"],
            [state: "Penang",           capital: "George Town"],
            [state: "Perak",            capital: "Ipoh"],
            [state: "Perlis",           capital: "Kangar"],
            [state: "Selangor",         capital: "Shah Alam"],
            [state: "Terengganu",       capital: "Kuala Terengganu"],
            [state: "Sabah",            capital: "Kota Kinabalu"],
            [state: "Sarawak",          capital: "Kuching"],
            [state: "Kuala Lumpur",     capital: "National Capital"],
            [state: "Labuan",           capital: "Offshore Financial Centre"],
            [state: "Putrajaya",        capital: "Federal Administrative Centre"]
    ]

    static Handler getStates = { RoutingContext routingContext ->
        JsonArray states = new JsonArray()
        STATES_DATA.each { entry ->
            states.add(new JsonObject().put("state", entry.state).put("capital", entry.capital))
        }

        routingContext.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end(new JsonObject().put("states", states).encodePrettily())
    }

    static Handler getCapitalByState = { RoutingContext routingContext ->
        String stateName = routingContext.request().getParam("state")

        if (stateName == null || stateName.trim().isEmpty()) {
            routingContext.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("error", "Query parameter 'state' is required").encodePrettily())
            return
        }

        Map found = STATES_DATA.find { it.state.equalsIgnoreCase(stateName) }

        if (found) {
            routingContext.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("state", found.state).put("capital", found.capital).encodePrettily())
        } else {
            routingContext.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("error", "State '${stateName}' not found").encodePrettily())
        }
    }

    static Handler getCapitalByStatePost = { RoutingContext routingContext ->
        JsonObject body = routingContext.getBodyAsJson()

        if (body == null || !body.containsKey("state")) {
            routingContext.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("error", "Request body must contain 'state'").encodePrettily())
            return
        }

        String stateName = body.getString("state")
        Map found = STATES_DATA.find { it.state.equalsIgnoreCase(stateName) }

        if (found) {
            routingContext.response()
                    .setStatusCode(200)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("state", found.state).put("capital", found.capital).encodePrettily())
        } else {
            routingContext.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().put("error", "State '${stateName}' not found").encodePrettily())
        }
    }
}
