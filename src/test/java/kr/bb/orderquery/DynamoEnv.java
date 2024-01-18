package kr.bb.orderquery;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

@ActiveProfiles("test")
public abstract class DynamoEnv {
    static final String DYNAMODB_IMAGE = "amazon/dynamodb-local:2.1.0";

    static final GenericContainer DYNAMODB_CONTAINER;

    static {
        DYNAMODB_CONTAINER = new GenericContainer<>(DYNAMODB_IMAGE)
                .withExposedPorts(8000)
                .withReuse(true);
        DYNAMODB_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        final String endpoint =
                String.format("http://%s:%s", DYNAMODB_CONTAINER.getHost(), DYNAMODB_CONTAINER.getMappedPort(8000));
        registry.add("aws.dynamodb.endpoint", () -> endpoint);
        registry.add("cloud.aws.credentials.ACCESS_KEY_ID", () -> "test");
        registry.add("cloud.aws.credentials.SECRET_ACCESS_KEY", () -> "test");
    }
}
