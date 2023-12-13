package kr.bb.orderquery;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class AbstractContainer {
    static final String DYNAMODB_IMAGE = "amazon/dynamodb-local:2.1.0";

    @ClassRule
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
        registry.add("cloud.aws.credentials.ACCESS_KEY_ID", () -> "testkey");
        registry.add("cloud.aws.credentials.SECRET_ACCESS_KEY", () -> "testkey");
    }
}
