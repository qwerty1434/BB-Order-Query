package kr.bb.orderquery.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;


@Configuration
@EnableDynamoDBRepositories(basePackages = "kr.bb.orderquery")
public class DynamoDbConfig {
    @Value("${aws.dynamodb.endpoint}")
    private String amazonDynamoDbEndpoint;

    @Value("${cloud.aws.region.static}")
    private String amazonDynamoDbRegion;

    @Value("${cloud.aws.credentials.ACCESS_KEY_ID}")
    private String amazonAwsAccessKey;

    @Value("${cloud.aws.credentials.SECRET_ACCESS_KEY}")
    private String amazonAwsSecretKey;

    @Primary
    @Bean
    public DynamoDBMapper dynamoDbMapper(AmazonDynamoDB amazonDynamoDb) {
        return new DynamoDBMapper(amazonDynamoDb, DynamoDBMapperConfig.DEFAULT);
    }

    @Bean(name = "amazonDynamoDB")
    public AmazonDynamoDB amazonDynamoDb() {
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(amazonAwsAccessKey, amazonAwsSecretKey));
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(amazonDynamoDbEndpoint, amazonDynamoDbRegion);

        return AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(endpointConfiguration).build();
    }

    // LocalDateTime Converter
    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<Date, LocalDateTime> {
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.toInstant(ZoneOffset.UTC));
        }

        @Override
        public LocalDateTime unconvert(Date source) {
            return LocalDateTime.ofInstant(source.toInstant(), ZoneId.of("UTC"));
        }
    }
    public static class LocalDateConverter implements DynamoDBTypeConverter<Date, LocalDate> {
        @Override
        public Date convert(LocalDate source) {
            return Date.from(source.atStartOfDay().toInstant(ZoneOffset.UTC));
        }

        @Override
        public LocalDate unconvert(Date source) {
            return LocalDate.ofInstant(source.toInstant(), ZoneId.of("UTC"));
        }
    }

}