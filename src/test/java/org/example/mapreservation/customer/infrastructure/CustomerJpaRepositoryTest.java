package org.example.mapreservation.customer.infrastructure;

import org.example.mapreservation.customer.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;

@SqlGroup({
        @Sql(value = "/sql/customer-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerJpaRepositoryTest {

    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @Test
    void 고객의_이메일로_고객을_조회할_수_있다() {
        // given
        String email = "abc@gmail.com";

        // when
        Customer customer = customerJpaRepository.findByEmail(email).orElseThrow();

        // then
        assertThat(customer.getId()).isEqualTo(1L);
        assertThat(customer.getEmail()).isEqualTo(email);
        assertThat(customer.getPassword()).isEqualTo("encodedpassword");
    }

}
