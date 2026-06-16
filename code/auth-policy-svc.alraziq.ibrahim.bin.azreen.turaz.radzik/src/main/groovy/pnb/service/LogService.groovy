package pnb.service


import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import java.util.concurrent.atomic.AtomicLong

class LogService {

    static Handler registerlogServiceHandler = { RoutingContext routingContext ->
        try {

            final AtomicLong counter = new AtomicLong(System.currentTimeMillis() * 100);
            long referenceId = counter.getAndIncrement()
            String referenceIdHeader = routingContext.request().headers().get('X-Reference-Id')

            if(referenceIdHeader){
                referenceId = referenceIdHeader.toBigInteger()
            }

            Map logObject = [
                    referenceId: referenceId.toString().take(10).toBigInteger(),
                    request    : [
                            normalizedPath: routingContext.normalisedPath(),
                            queryParams   : routingContext.queryParams().collectEntries(),
                            //parsedHeaders : routingContext.parsedHeaders(),
                            session       : routingContext.session(),
                            method        : routingContext.request().method(),
                            pathParams    : routingContext.pathParams(),
                            headers       : routingContext.request().headers().collectEntries(),
                            body          : routingContext.request().method() != HttpMethod.GET ? routingContext.getBodyAsString() : "",
                            //url           : routingContext.request().uri()
                    ]
            ]
            routingContext.put("logObject", new JsonObject(logObject))
            routingContext.next()

        } catch (e) {
            println e.stackTrace
        }

    }

    static logServiceFlushHandler(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking({
            try {

                JsonObject application = routingContext.get('configApp')
                JsonObject elastic = application.getJsonObject('application').getJsonObject('elastic')
                String elasticHost = elastic.getString('elasticHost')
                String username = elastic.getString('username')
                String password = elastic.getString('password')
                WebClient webClient = routingContext.get('webClient')
                JsonObject logObject = routingContext.get('logObject')
                String elasticIndex = elastic.getString('indexPrefix') + new Date().format('yyyy.MM')
                JsonObject logPrimary = new JsonObject().put('api_log', logObject).put('date_posted', new Date().getTime())
                if (logObject) {
                    //println logObject.encode()
                    webClient.postAbs(elasticHost + "/" + elasticIndex + "/" + "_doc")
                            .basicAuthentication(username, password)
                            .sendJson(logPrimary, { resp ->
                                if (resp.succeeded()) {
                                    println resp.result().bodyAsJsonObject()
                                } else {
                                    println "Failed: " + resp.cause().message
                                }
                            })

                } else {
                    println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
                }

            } catch (e) {
                println e.stackTrace
            }
        }, {

        })

    }

    static log(String key, JsonObject jsonObject, RoutingContext routingContext) {
        try {

            JsonObject logObjectCurrentRequest = routingContext.get('logObject')

            //TODO: Temporary Solution, i dont know why 'out' is added in. The data type is printwriter making the logServiceFlushHandler to fail due to object not able to be as a string .
            // To be investigated. Happens only during Redemption Successful
            if (key == 'apigeeRedemptionCashPayload') {
                Map js = jsonObject
                js.remove('out')
            }

            //

            if (logObjectCurrentRequest) {
                routingContext.put('logObject', logObjectCurrentRequest.put(key, jsonObject))
            } else {
                println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
            }

        } catch (e) {
            println e.stackTrace
        }
    }

    static log(String key, Exception ex, RoutingContext routingContext) {
        try {

            JsonObject stacktraceError = new JsonObject()
            stacktraceError.put("type", "Exceptions")
            stacktraceError.put("message", ex.getMessage())
            stacktraceError.put("stacktrace", getStackTrace(ex))
            JsonObject logObjectCurrentRequest = routingContext.get('logObject')
            if (logObjectCurrentRequest) {
                routingContext.put('logObject', logObjectCurrentRequest.put(key, stacktraceError))
            } else {
                println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
            }

        } catch (e) {
            println e.stackTrace
        }
    }

    static log(String key, Throwable ex, RoutingContext routingContext) {
        try {

            JsonObject stacktraceError = new JsonObject()
            stacktraceError.put("type", "Exceptions")
            stacktraceError.put("message", ex.getMessage())
            stacktraceError.put("stacktrace", getStackTrace(ex))
            JsonObject logObjectCurrentRequest = routingContext.get('logObject')
            if (logObjectCurrentRequest) {
                routingContext.put('logObject', logObjectCurrentRequest.put(key, stacktraceError))
            } else {
                println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
            }

        } catch (e) {
            println e.stackTrace
        }


    }

    static logServiceFlush(JsonObject jsonObject, RoutingContext routingContext) {
        routingContext.vertx().executeBlocking({
            try {

                JsonObject application = routingContext.get('configApp')
                JsonObject elastic = application.getJsonObject('application').getJsonObject('elastic')
                String elasticHost = elastic.getString('elasticHost')
                String username = elastic.getString('username')
                String password = elastic.getString('password')
                WebClient webClient = routingContext.get('webClient')
                String elasticIndex = elastic.getString('indexPrefix') + new Date().format('yyyy.MM')
                JsonObject logPrimary = new JsonObject().put('api_log', jsonObject).put('date_posted', new Date().getTime())
                if (jsonObject) {
                    //println logObject.encode()
                    webClient.postAbs(elasticHost + "/" + elasticIndex + "/" + "_doc")
                            .basicAuthentication(username, password)
                            .sendJson(logPrimary, { resp ->
                                if (resp.succeeded()) {
                                    println resp.result().bodyAsJsonObject()
                                } else {
                                    println "Failed: " + resp.cause().message
                                }
                            })
                } else {
                    println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
                }

            } catch (e) {
                println e.stackTrace
            }
        }, {

        })
    }

    static logAsync(String key, JsonObject jsonObject, RoutingContext routingContext) {

        try {

            JsonObject logObjectCurrentRequest = routingContext.get('logObject')

            //TODO: Temporary Solution, i dont know why 'out' is added in. The data type is printwriter making the logServiceFlushHandler to fail due to object not able to be as a string .
            // To be investigated. Happens only during Redemption Successful
            if (key == 'apigeeRedemptionCashPayload') {
                Map js = jsonObject
                js.remove('out')
            }

            //

            if (logObjectCurrentRequest) {
                logObjectCurrentRequest.put("referenceId", routingContext.get('logObject').get('referenceId'))
                logServiceFlush(logObjectCurrentRequest.put(key, jsonObject), routingContext)
            } else {
                println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
            }

        } catch (e) {
            println e.stackTrace
        }
    }

    static logAsync(String key, Exception ex, RoutingContext routingContext) {
        try {

            JsonObject stacktraceError = new JsonObject()
            routingContext.put("referenceId", routingContext.get('logObject').get('referenceId'))
            stacktraceError.put("type", "Exceptions")
            stacktraceError.put("message", ex.getMessage())
            stacktraceError.put("stacktrace", getStackTrace(ex))
            JsonObject logObjectCurrentRequest = routingContext.get('logObject')
            if (logObjectCurrentRequest) {
                logServiceFlush(logObjectCurrentRequest.put(key, stacktraceError), routingContext)
            } else {
                println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
            }

        } catch (e) {
            println e.stackTrace
        }
    }

    static logAsync(String key, Throwable ex, RoutingContext routingContext) {
        try {

            JsonObject stacktraceError = new JsonObject()
            routingContext.put("referenceId", routingContext.get('logObject').get('referenceId'))
            stacktraceError.put("type", "Exceptions")
            stacktraceError.put("message", ex.getMessage())
            stacktraceError.put("stacktrace", getStackTrace(ex))
            JsonObject logObjectCurrentRequest = routingContext.get('logObject')
            if (logObjectCurrentRequest) {
                logServiceFlush(logObjectCurrentRequest.put(key, stacktraceError), routingContext)
            } else {
                println "Warning: Please add registerlogServiceHandler as one of your request handler at router for this request."
            }

        } catch (e) {
            println e.stackTrace
        }
    }

    static String getStackTrace(final Throwable throwable) {
        try {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            throwable.printStackTrace(pw);
            return sw.getBuffer().toString();
        } catch (e) {
            println e.stackTrace
        }
    }

}
