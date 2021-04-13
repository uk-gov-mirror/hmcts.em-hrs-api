package uk.gov.hmcts.reform.em.hrs;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Sql(scripts = "/data/populate-db.sql")
public class GetFileNamesScenarios extends AbstractBaseScenarios {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testShouldPopulateDatabaseTables() {
        final int expectedRowCount = 3;
        final String table = "job_in_progress";

        final int actualRowCount = JdbcTestUtils.countRowsInTable(this.jdbcTemplate, table);

        assertThat(actualRowCount, is(expectedRowCount));
    }

}
