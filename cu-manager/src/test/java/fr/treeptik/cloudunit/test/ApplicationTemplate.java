package fr.treeptik.cloudunit.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.json.Json;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.dto.ApplicationResource;

public class ApplicationTemplate {
    private final MockMvc mockMvc;
    private final MockHttpSession session;
    
    public ApplicationTemplate(MockMvc mockMvc, MockHttpSession session) {
        this.mockMvc = mockMvc;
        this.session = session;
    }
    
    public ApplicationResource getApplication(ResultActions result) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readValue(content, ApplicationResource.class);
    }
    
    public ResultActions createApplication(String displayName, String serverType) throws Exception {
        String request = Json.createObjectBuilder()
                .add("displayName", displayName)
                .add("serverType", serverType)
                .build().toString();
                
        ResultActions result = mockMvc.perform(post("/applications")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        return result;
    }
    
    public ApplicationResource createAndAssumeApplication(String displayName, String serverType) throws Exception {
        ResultActions result = createApplication(displayName, serverType);
        result.andExpect(status().isCreated());
        
        return getApplication(result);
    }
    
    public ApplicationResource refreshApplication(ApplicationResource application) throws Exception {
        String url = application.getId().getHref();
        
        ResultActions result = mockMvc.perform(get(url).session(session));
        result.andExpect(status().isOk());
        
        return getApplication(result);
    }
    
    public ResultActions removeApplication(ApplicationResource application) throws Exception {
        String url = application.getId().getHref();
        ResultActions result = mockMvc.perform(delete(url).session(session));
        return result;
    }
    
    public ResultActions stopApplication(ApplicationResource application) throws Exception {
        String url = application.getLink("stop").getHref();
        ResultActions result = mockMvc.perform(post(url).session(session));
        return result;
    }

    public ResultActions startApplication(ApplicationResource application) throws Exception {
        String url = application.getLink("start").getHref();
        ResultActions result = mockMvc.perform(post(url).session(session));
        return result;
    }

    public ResultActions deleteApplication(ApplicationResource application) throws Exception {
        String url = application.getId().getHref();
        ResultActions result = mockMvc.perform(delete(url).session(session));
        return result;
    }
}