package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalServiceImplTest {

    @Mock
    PatientInfoFileRepository patientInfoFileRepository;
    @Mock
    SendAlertService alertService;
    @InjectMocks
    MedicalServiceImpl medicalService;
    @Captor
    ArgumentCaptor<String> argumentCaptor;


    @ParameterizedTest
    @CsvSource({
            "id1,100,40",
            "id2,120,90",
            "id3,40,80"
    })
    void checkBloodPressure(String id, int high, int low) {
        when(patientInfoFileRepository.getById(id))
                .thenReturn(new PatientInfo(id, "Gnom", "Gnomych"
                        , LocalDate.of(1996, 6, 18)
                        , new HealthInfo(BigDecimal.valueOf(40), new BloodPressure(high, low))));
        medicalService.checkBloodPressure(id, new BloodPressure(60, 40));
        //
        Mockito.verify(alertService).send(argumentCaptor.capture());
        String expected = "Warning, patient with id: " + id + ", need help";
        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    @ParameterizedTest
    @CsvSource({
            "id1,41.6",
            "id2,40.5",
            "id3,34.8"
    })
    void checkTemperature(String id, double temperature) {
        when(patientInfoFileRepository.getById(id))
                .thenReturn(new PatientInfo(id, "Gnom", "Gnomych"
                        , LocalDate.of(1996, 6, 18)
                        , new HealthInfo(BigDecimal.valueOf(temperature), new BloodPressure(40, 80))));
        medicalService.checkTemperature(id, new BigDecimal("36.9"));
    }

    @ParameterizedTest
    @CsvSource({
            "id1,60,40"
    })
    void checkBloodPressureIfGood(String id, int high, int low) {
        when(patientInfoFileRepository.getById(id))
                .thenReturn(new PatientInfo(id, "Gnom", "Gnomych"
                        , LocalDate.of(1996, 6, 18)
                        , new HealthInfo(BigDecimal.valueOf(40), new BloodPressure(high, low))));
        //
        medicalService.checkBloodPressure(id, new BloodPressure(60, 40));
        //
        verifyNoInteractions(alertService);
    }


}