package br.com.social.core.swagger;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title="Api rede social com Quarkus",
                version = "1.0.1",
                contact = @Contact(
                        name = "Lucas Galvao",
                        url = "https://github.com/lucasengcomp/social-quarkus",
                        email = "lucas.engcomp@outlook.com"),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html")
))
public class ConfigApplication extends Application {
}
