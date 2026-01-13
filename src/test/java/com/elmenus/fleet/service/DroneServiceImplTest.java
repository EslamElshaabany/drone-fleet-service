package com.elmenus.fleet.service;

import com.elmenus.fleet.dto.DroneDTO;
import com.elmenus.fleet.dto.LoadDTO;
import com.elmenus.fleet.entity.Drone;
import com.elmenus.fleet.entity.DroneLoad;
import com.elmenus.fleet.entity.DroneModel;
import com.elmenus.fleet.entity.Medication;
import com.elmenus.fleet.exception.*;
import com.elmenus.fleet.repository.DroneLoadRepository;
import com.elmenus.fleet.repository.DroneModelRepository;
import com.elmenus.fleet.repository.DroneRepository;
import com.elmenus.fleet.repository.MedicationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DroneServiceImplTest {

  @Mock
  private DroneRepository droneRepository;

  @Mock
  private DroneLoadRepository droneLoadRepository;

  @Mock
  private MedicationRepository medicationRepository;

  @Mock
  private DroneModelRepository droneModelRepository;

  @InjectMocks
  private DroneServiceImpl droneService;

  @Nested
  class RegisterDroneTests {

    @Test
    void registerDrone_Success() {
      // Arrange
      DroneDTO droneDTO = new DroneDTO("DR0123", 50, Drone.DroneStatus.IDLE, "Lightweight");
      DroneModel droneModel = new DroneModel("Lightweight", 200d);
      when(droneModelRepository.findByModel("Lightweight")).thenReturn(droneModel);
      when(droneRepository.existsBySerialNumber("DR0123")).thenReturn(false);
      when(droneRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      // Act
      Drone result = droneService.registerDrone(droneDTO);

      // Assert
      assertNotNull(result);
      assertEquals("DR0123", result.getSerialNumber());
      assertEquals(Drone.DroneStatus.IDLE, result.getStatus());
      verify(droneRepository, times(1)).save(any());
    }

    @Test
    void registerDrone_DroneWithSerialNumberExists() {
      // Arrange
      DroneDTO droneDTO = new DroneDTO("DR0123", 50, Drone.DroneStatus.IDLE, "Lightweight");
      when(droneRepository.existsBySerialNumber("DR0123")).thenReturn(true);

      // Act + Assert
      assertThrows(DuplicateSerialNumberException.class, () -> droneService.registerDrone(droneDTO));
      verify(droneRepository, never()).save(any());
    }

    @Test
    void registerDrone_DroneModelNotFound() {
      // Arrange
      DroneDTO droneDTO = new DroneDTO("DR0123", 50, Drone.DroneStatus.IDLE, "UnknownModel");
      when(droneModelRepository.findByModel("UnknownModel")).thenReturn(null);

      // Act + Assert
      assertThrows(NotFoundException.class, () -> droneService.registerDrone(droneDTO));
      verify(droneRepository, never()).save(any());
    }
  }

  @Nested
  class LoadDroneTests {
    @Test
    void loadDrone_Success() {
      // Arrange
      DroneModel droneModel = new DroneModel("Lightweight", 200d);
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setSerialNumber("DR0123");
      drone.setStatus(Drone.DroneStatus.IDLE);
      drone.setBatteryCapacity(50);
      drone.setDroneModel(droneModel);

      LoadDTO loadDTO = new LoadDTO(Arrays.asList("MED001", "MED006"));
      Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
      Medication med2 = new Medication("MED006", "Band_Aid", 50d, "https://example.com/band_aid_image.jpg");

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
      when(medicationRepository.findByCode("MED001")).thenReturn(med1);
      when(medicationRepository.findByCode("MED006")).thenReturn(med2);
      when(droneLoadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(droneRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      // Act
      Drone result = droneService.loadDrone(1L, loadDTO);

      // Assert
      assertNotNull(result);
      assertEquals(Drone.DroneStatus.LOADED, result.getStatus());
      verify(droneLoadRepository, times(1)).save(any());
      verify(droneRepository, times(1)).save(any());
    }

    @Test
    void loadDrone_DroneNotIdle() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setStatus(Drone.DroneStatus.LOADING);

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

      // Act + Assert
      assertThrows(DroneLoadingException.class, () -> droneService.loadDrone(1L, new LoadDTO(Collections.emptyList())));
      verify(droneLoadRepository, never()).save(any());
      verify(droneRepository, never()).save(any());
    }

    @Test
    void loadDrone_MedicationNotFound() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setSerialNumber("DR0123");
      drone.setStatus(Drone.DroneStatus.IDLE);
      drone.setBatteryCapacity(50);
      drone.setDroneModel(new DroneModel("Lightweight", 200d));

      LoadDTO loadDTO = new LoadDTO(Arrays.asList("MED001", "UnknownMedication"));
      Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
      when(medicationRepository.findByCode("MED001")).thenReturn(med1);
      when(medicationRepository.findByCode("UnknownMedication")).thenReturn(null);

      // Act + Assert
      assertThrows(NotFoundException.class, () -> droneService.loadDrone(1L, loadDTO));
      verify(droneLoadRepository, never()).save(any());
      verify(droneRepository, never()).save(any());
    }

    @Test
    void loadDrone_ExceedsMaxWeight() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setSerialNumber("DR0123");
      drone.setStatus(Drone.DroneStatus.IDLE);
      drone.setBatteryCapacity(50);
      drone.setDroneModel(new DroneModel("Lightweight", 200d));

      LoadDTO loadDTO = new LoadDTO(Arrays.asList("MED001", "MED002"));
      Medication med1 = new Medication("MED001", "Aspirin", 150d, "https://example.com/aspirin_image.jpg");
      Medication med2 = new Medication("MED002", "Band_Aid", 60d, "https://example.com/band_aid_image.jpg");

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
      when(medicationRepository.findByCode("MED001")).thenReturn(med1);
      when(medicationRepository.findByCode("MED002")).thenReturn(med2);

      // Act + Assert
      assertThrows(DroneLoadingException.class, () -> droneService.loadDrone(1L, loadDTO));
      verify(droneLoadRepository, times(1)).save(any());
      verify(droneRepository, never()).save(any());
    }


    @Test
    void loadDrone_LowBatteryCapacity() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setSerialNumber("DR0123");
      drone.setStatus(Drone.DroneStatus.IDLE);
      drone.setBatteryCapacity(10);
      drone.setDroneModel(new DroneModel("Lightweight", 200d));

      LoadDTO loadDTO = new LoadDTO(Arrays.asList("MED001", "MED002"));
      Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
      Medication med2 = new Medication("MED002", "Band_Aid", 50d, "https://example.com/band_aid_image.jpg");

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
      when(medicationRepository.findByCode("MED001")).thenReturn(med1);
      when(medicationRepository.findByCode("MED002")).thenReturn(med2);

      // Act + Assert
      assertThrows(DroneLoadingException.class, () -> droneService.loadDrone(1L, loadDTO));
      verify(droneLoadRepository, times(1)).save(any());
      verify(droneRepository, never()).save(any());
    }

    @Test
    void loadDrone_EmptyLoad() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setStatus(Drone.DroneStatus.IDLE);

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

      // Act + Assert
      assertThrows(LoadEmptyException.class, () -> droneService.loadDrone(1L, new LoadDTO(Collections.emptyList())));
      verify(droneLoadRepository, never()).save(any());
      verify(droneRepository, never()).save(any());
    }

    @Test
    void loadDrone_MaxLoadWeight() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setStatus(Drone.DroneStatus.IDLE);
      drone.setBatteryCapacity(50);
      drone.setDroneModel(new DroneModel("Lightweight", 200d));

      LoadDTO loadDTO = new LoadDTO(Arrays.asList("MED001", "MED002"));
      Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
      Medication med2 = new Medication("MED002", "Band_Aid", 100d, "https://example.com/band_aid_image.jpg");

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
      when(medicationRepository.findByCode("MED001")).thenReturn(med1);
      when(medicationRepository.findByCode("MED002")).thenReturn(med2);

      // Act + Assert
      assertDoesNotThrow(() -> droneService.loadDrone(1L, loadDTO));
      verify(droneLoadRepository, times(1)).save(any());
      verify(droneRepository, times(1)).save(any());
    }

  }

  @Nested
  class GetLoadedMedicationsTests {
    @Test
    void getLoadedMedications_Success() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setStatus(Drone.DroneStatus.LOADED);

      DroneLoad droneLoad = new DroneLoad();
      droneLoad.setId(1L);
      droneLoad.setStatus(DroneLoad.LoadStatus.ASSIGNED);

      Medication med1 = new Medication("MED001", "Aspirin", 100d, "https://example.com/aspirin_image.jpg");
      Medication med2 = new Medication("MED002", "Band_Aid", 50d, "https://example.com/band_aid_image.jpg");

      droneLoad.setMedications(Arrays.asList(med1, med2));
      drone.setDroneLoad(droneLoad);

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

      // Act
      List<Medication> result = droneService.getLoadedMedications(1L);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals("MED001", result.get(0).getCode());
      assertEquals("MED002", result.get(1).getCode());
    }

    @Test
    void getLoadedMedications_DroneWithoutLoad() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setStatus(Drone.DroneStatus.IDLE);

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

      // Act + Assert
      assertThrows(DroneNotLoadedException.class, () -> droneService.getLoadedMedications(1L));
      verify(droneLoadRepository, never()).save(any());
    }

    @Test
    void getLoadedMedications_NonExistentDrone() {
      // Arrange
      when(droneRepository.findById(1L)).thenReturn(Optional.empty());

      // Act + Assert
      assertThrows(NotFoundException.class, () -> droneService.getLoadedMedications(1L));
      verify(droneLoadRepository, never()).save(any());
    }
  }

  @Nested
  class GetAvailableDrones {

    @Test
    void getAvailableDrones_WithStatus() {
      // Arrange
      Drone drone1 = new Drone();
      drone1.setId(1L);
      drone1.setStatus(Drone.DroneStatus.IDLE);

      Drone drone2 = new Drone();
      drone2.setId(2L);
      drone2.setStatus(Drone.DroneStatus.LOADED);

      when(droneRepository.findByStatus(Drone.DroneStatus.IDLE)).thenReturn(Collections.singletonList(drone1));

      // Act
      List<Drone> result = droneService.getAvailableDrones(Drone.DroneStatus.IDLE);

      // Assert
      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals(Drone.DroneStatus.IDLE, result.get(0).getStatus());
    }


    @Test
    void getAvailableDrones_NullStatus() {
      // Arrange
      Drone drone1 = new Drone();
      drone1.setId(1L);
      drone1.setStatus(Drone.DroneStatus.IDLE);

      Drone drone2 = new Drone();
      drone2.setId(2L);
      drone2.setStatus(Drone.DroneStatus.LOADED);

      when(droneRepository.findAll()).thenReturn(Arrays.asList(drone1, drone2));

      // Act
      List<Drone> result = droneService.getAvailableDrones(null);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals(Drone.DroneStatus.IDLE, result.get(0).getStatus());
      assertEquals(Drone.DroneStatus.LOADED, result.get(1).getStatus());
    }
  }

  @Nested
  class CheckBatteryLevel {

    @Test
    void checkBatteryLevel_Success() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setBatteryCapacity(80);

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

      // Act
      Integer result = droneService.checkBatteryLevel(1L);

      // Assert
      assertNotNull(result);
      assertEquals(80, result);
    }

    @Test
    void checkBatteryLevel_DroneNotFound() {
      // Arrange
      when(droneRepository.findById(1L)).thenReturn(Optional.empty());

      // Act + Assert
      assertThrows(NotFoundException.class, () -> droneService.checkBatteryLevel(1L));
    }

    @Test
    void checkBatteryLevel_MaximumCapacity() {
      // Arrange
      Drone drone = new Drone();
      drone.setId(1L);
      drone.setBatteryCapacity(100);

      when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

      // Act
      Integer result = droneService.checkBatteryLevel(1L);

      // Assert
      assertNotNull(result);
      assertEquals(100, result);
    }
  }

}
