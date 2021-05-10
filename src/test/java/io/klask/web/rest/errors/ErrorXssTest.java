package io.klask.web.rest.errors;

import io.klask.KlaskApp;
import io.klask.service.IndexService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KlaskApp.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ErrorXssTest extends TestCase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IndexService indexService;

    @Test
    public void testForXssInSearchInput() throws Exception {
        indexService.initIndexes();
        //?page=0&project=&query=testfr"><img/src="X"/onerror=alert(document.domain)>&size=10&sort=_score,desc,
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/_search/files")
                .param("page","0")
                .param("project","")
                .param("query","testfr\"><img/src=\"X\"/onerror=alert(document.domain)>"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("<"))));
    }

}
