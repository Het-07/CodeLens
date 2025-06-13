package codelens.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * MongoConfig class configures MongoDB-related beans for validation.
 * This configuration enables JSR-303/JSR-380 validation for MongoDB entities.
 */
@Configuration
public class MongoConfig {

	/**
	 * Creates and returns a LocalValidatorFactoryBean, which is used for
	 * validating MongoDB entities using JSR-303/JSR-380 annotations.
	 *
	 * @return A LocalValidatorFactoryBean instance.
	 */
	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	/**
	 * Creates and returns a ValidatingMongoEventListener that triggers validation
	 * on MongoDB entities before they are persisted.
	 *
	 * @param factory The LocalValidatorFactoryBean that will be used for validation.
	 * @return A ValidatingMongoEventListener that performs validation before MongoDB events.
	 */
	@Bean
	public ValidatingMongoEventListener validatingMongoEventListener(LocalValidatorFactoryBean factory) {
		return new ValidatingMongoEventListener(factory);
	}
}
