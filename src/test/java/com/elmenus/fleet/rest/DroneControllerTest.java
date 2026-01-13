package com.elmenus.fleet.rest;

import com.elmenus.fleet.dto.DroneDTO;
import com.elmenus.fleet.dto.LoadDTO;
import com.elmenus.fleet.entity.Drone;
import com.elmenus.fleet.entity.DroneLoad;
import com.elmenus.fleet.entity.DroneModel;
import com.elmenus.fleet.entity.Medication;
import com.elmenus.fleet.service.DroneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;


@WebMvcTest(DroneController.class)
public class DroneControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DroneService droneService;

  @InjectMocks
  private DroneController droneController;


  @Test
  void testRegisterDrone() throws Exception {
    DroneDTO droneDTO = new DroneDTO("DR0123", 50, Drone.DroneStatus.IDLE, "Lightweight");
    DroneModel droneModel = new DroneModel("LightWeight", 200d);
    droneModel.setId(1L);
    Drone registeredDrone = new Drone("DR0123", 50, Drone.DroneStatus.IDLE, droneModel);
    registeredDrone.setId(1L);
    when(droneService.registerDrone(droneDTO)).thenReturn(registeredDrone);

    mockMvc.perform(MockMvcRequestBuilders.post("/drones")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(droneDTO)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void testLoadDrone() throws Exception {
    Long droneId = 1L;
    LoadDTO loadDTO = new LoadDTO(Arrays.asList("MED001", "MED006"));
    Drone loadedDrone = new Drone("DR0123", 50, Drone.DroneStatus.IDLE, new DroneModel("LightWeight", 200d));
    Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
    Medication med2 = new Medication("MED006", "Band_Aid", 50d, "https://example.com/band_aid_image.jpg");
    DroneLoad droneLoad = new DroneLoad();
    droneLoad.setWeight(med1.getWeight() + med2.getWeight());
    droneLoad.setStatus(DroneLoad.LoadStatus.ASSIGNED);
    droneLoad.setMedications(Arrays.asList(med1, med2));
    loadedDrone.setDroneLoad(droneLoad);
    when(droneService.loadDrone(droneId, loadDTO)).thenReturn(loadedDrone);

    mockMvc.perform(MockMvcRequestBuilders.post("/drones/{id}/load", droneId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(loadDTO)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void testGetLoadedMedications() throws Exception {
    Long droneId = 1L;
    Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
    Medication med2 = new Medication("MED006", "Band_Aid", 50d, "https://example.com/band_aid_image.jpg");
    List<Medication> medications = Arrays.asList(med1, med2);
    when(droneService.getLoadedMedications(droneId)).thenReturn(medications);

    mockMvc.perform(MockMvcRequestBuilders.get("/drones/{id}/load", droneId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(medications)));
  }

  @Test
  void testGetAvailableDrones() throws Exception {
    Drone drone1 = new Drone("DR0123", 50, Drone.DroneStatus.IDLE, new DroneModel("LightWeight", 200d));
    Drone drone2 = new Drone("DR0456", 75, Drone.DroneStatus.LOADED, new DroneModel("HeavyWeight", 500d));
    Drone drone3 = new Drone("DR0789", 60, Drone.DroneStatus.IDLE, new DroneModel("CruiserWeight", 400d));
    Drone drone4 = new Drone("DR1011", 80, Drone.DroneStatus.LOADED, new DroneModel("MiddleWeight", 300d));
    List<Drone> drones = Arrays.asList(drone1, drone2, drone3, drone4);
    when(droneService.getAvailableDrones(null)).thenReturn(drones);

    mockMvc.perform(MockMvcRequestBuilders.get("/drones")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(drones)));
  }

  @Test
  void testGetAvailableDronesWithStatus() throws Exception {
    Drone drone1 = new Drone("DR0123", 50, Drone.DroneStatus.IDLE, new DroneModel("LightWeight", 200d));
    Drone drone2 = new Drone("DR0456", 75, Drone.DroneStatus.IDLE, new DroneModel("HeavyWeight", 500d));
    Drone drone3 = new Drone("DR0789", 60, Drone.DroneStatus.IDLE, new DroneModel("CruiserWeight", 400d));
    Drone drone4 = new Drone("DR1011", 80, Drone.DroneStatus.IDLE, new DroneModel("MiddleWeight", 300d));
    List<Drone> drones = Arrays.asList(drone1, drone2, drone3, drone4);
    when(droneService.getAvailableDrones(Drone.DroneStatus.IDLE)).thenReturn(drones);

    mockMvc.perform(MockMvcRequestBuilders.get("/drones")
            .param("status", "IDLE")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(drones)));
  }

  @Test
  void testCheckBatteryLevel() throws Exception {
    Long droneId = 1L;
    int batteryLevel = 80;
    when(droneService.checkBatteryLevel(droneId)).thenReturn(batteryLevel);

    mockMvc.perform(MockMvcRequestBuilders.get("/drones/{id}/battery", droneId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(batteryLevel)));
  }
}