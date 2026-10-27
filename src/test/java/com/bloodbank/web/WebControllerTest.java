package com.bloodbank.web;

import com.bloodbank.domain.Donor;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.Urgency;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.RecipientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.bloodbank.TestData.*;
import static com.bloodbank.domain.BloodGroup.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WebControllerTest {

    @Autowired MockMvc mvc;
    @Autowired DonorRepository donorRepository;
    @Autowired RecipientRepository recipientRepository;

    @Test
    void allPagesRender() throws Exception {
        Donor d = donorRepository.save(donor("Page Donor", O_NEG, TEMPE_LAT, TEMPE_LON));
        Recipient r = recipientRepository.save(recipient("Page Patient", A_POS, Urgency.HIGH, TEMPE_LAT, TEMPE_LON));
        for (String url : new String[]{"/", "/donors", "/donors/new", "/donors/" + d.getId(),
                "/donors/" + d.getId() + "/edit", "/recipients", "/recipients/new",
                "/recipients/" + r.getId(), "/recipients/" + r.getId() + "/edit"}) {
            mvc.perform(get(url)).andExpect(status().isOk());
        }
    }

    @Test
    void validDonorRedirectsInvalidDonorShowsErrors() throws Exception {
        mvc.perform(post("/donors")
                        .param("name", "New Donor").param("age", "30").param("bloodGroup", "O_NEG")
                        .param("phone", "(480) 555-0199").param("email", "new@example.com")
                        .param("city", "Tempe").param("latitude", "33.42").param("longitude", "-111.94"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/donors").param("name", "Kid").param("age", "15").param("email", "bad"))
                .andExpect(status().isOk())
                .andExpect(view().name("donors/form"))
                .andExpect(model().attributeHasFieldErrors("donor", "age", "email", "bloodGroup", "latitude"));
    }

    @Test
    void requestPageShowsOnlyCompatibleDonors() throws Exception {
        donorRepository.save(donor("Compatible Carla", O_NEG, 33.4148, -111.9093));
        donorRepository.save(donor("Incompatible Ivan", B_POS, 33.4148, -111.9093));
        Recipient r = recipientRepository.save(recipient("Patient", A_NEG, Urgency.CRITICAL, TEMPE_LAT, TEMPE_LON));

        mvc.perform(get("/recipients/" + r.getId()))
                .andExpect(content().string(containsString("Compatible Carla")))
                .andExpect(content().string(not(containsString("Incompatible Ivan"))));
    }

    @Test
    void missingDonorIs404() throws Exception {
        mvc.perform(get("/donors/999999")).andExpect(status().isNotFound());
    }
}