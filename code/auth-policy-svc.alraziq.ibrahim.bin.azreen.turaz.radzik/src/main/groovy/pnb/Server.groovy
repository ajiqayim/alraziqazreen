package pnb

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.VertxOptions
import io.vertx.core.WorkerExecutor
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.jwt.JWTOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.redis.RedisClient
import io.vertx.redis.RedisOptions
import pnb.handler.PasswordValidator
import pnb.handler.StateHandler
import pnb.service.LogService

import io.vertx.core.json.JsonArray

import java.util.concurrent.TimeUnit

class Server extends AbstractVerticle {

    static WorkerExecutor workerExecutor

    static HttpServer vertxHttpServer

    static Router router

    JsonObject configApp

    static JWTAuth jwtInternal

    RedisClient redisClient

    WebClient webClient

    public void start() {
        println("Starting")

        workerExecutor = getVertx().createSharedWorkerExecutor("Worker Pool", 2000, 5, TimeUnit.MINUTES)

        vertxHttpServer = getVertx().createHttpServer(HttpServerOptions.newInstance())

        router = Router.router(getVertx())

        vertxHttpServer.requestHandler(router).listen(8080, { asyncResult ->
            if (asyncResult.succeeded()) {
                println "Succeded in binding to port : " + asyncResult.result().actualPort()
            } else {
                println "Failed to bind to port"
            }
        })

        getVertx().createHttpClient()

        ConfigRetriever retriever = ConfigRetriever.create(getVertx(),
                ConfigRetrieverOptions.newInstance()
                        .addStore(ConfigStoreOptions.newInstance()
                                .setFormat('json')
                                .setType('file')
                                .setConfig(new JsonObject([path: "config/config.json"]))
                        )
        )

        retriever.getConfig({ json ->
            this.configApp = json.result()
            println "Init Config..."

            //Set Config for Redis
            JsonObject redisOptions = configApp.getValue("redis")
            redisClient = RedisClient.create(vertx, new RedisOptions(redisOptions))
            webClient = WebClient.create(vertx)
        })

        retriever.listen({ change ->
            this.configApp = change.newConfiguration

            println "New Config updated..."
            println configApp
        })


        Handler appContextHandler = { RoutingContext routingContext ->
            routingContext.put("configApp", configApp)
            routingContext.put("jwtInternal", jwtInternal)
            routingContext.put("redisClient", redisClient)
            routingContext.put("webClient", webClient)
            routingContext.next()
        }

        router.route().handler(BodyHandler.create(true)
                .setDeleteUploadedFilesOnEnd(true))
        router.route().handler(LogService.registerlogServiceHandler)

        router.get("/credentialMembersCheck")
                .handler(appContextHandler)
                .handler(PasswordValidator.credentialMembersCheck)

        router.get("/preCredentialNonMembersCheck")
                .handler(appContextHandler)
                .handler(PasswordValidator.preCredentialNonMembersCheck)

        router.get("/preCredentialNonMembersCheckUsername")
                .handler(appContextHandler)
                .handler(PasswordValidator.preCredentialNonMembersCheckUsername)

        router.get("/preCredentialNonMembersCheckPassword")
                .handler(appContextHandler)
                .handler(PasswordValidator.preCredentialNonMembersCheckPassword)

        router.get("/securePhraseMembersCheck")
                .handler(appContextHandler)
                .handler(PasswordValidator.securePhraseMembersCheck)

        router.get("/api/states")
                .handler(appContextHandler)
                .handler(StateHandler.getStates)

        router.get("/api/states/capital")
                .handler(appContextHandler)
                .handler(StateHandler.getCapitalByState)

        router.post("/api/states/capital")
                .handler(appContextHandler)
                .handler(StateHandler.getCapitalByStatePost)
    }

    public void stop() {
        println("Stopping")
    }
}