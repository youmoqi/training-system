package com.training.service;

import com.training.entity.RegistrationConfig;
import com.training.repository.RegistrationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrationConfigService {
    @Autowired
    private RegistrationConfigRepository repository;

    public RegistrationConfig getOrCreate() {
        return repository.findAll().stream().findFirst().orElseGet(() -> {
            RegistrationConfig cfg = new RegistrationConfig();
            // default: username/password/realName/phone/idCard required; others optional; editable false except password/phone
            cfg.setFieldsConfigJson("{\"username\":{\"required\":true,\"editable\":false},\"password\":{\"required\":true,\"editable\":true},\"realName\":{\"required\":true,\"editable\":false},\"gender\":{\"required\":false,\"editable\":true},\"idCard\":{\"required\":true,\"editable\":false},\"phone\":{\"required\":true,\"editable\":true},\"workUnit\":{\"required\":false,\"editable\":true},\"trainingType\":{\"required\":false,\"editable\":true},\"jobCategory\":{\"required\":false,\"editable\":true},\"facePhotoUrl\":{\"required\":false,\"editable\":true},\"paymentAmount\":{\"required\":false,\"editable\":true}}");
            return repository.save(cfg);
        });
    }

    public RegistrationConfig update(String json) {
        RegistrationConfig cfg = getOrCreate();
        cfg.setFieldsConfigJson(json);
        return repository.save(cfg);
    }
} 