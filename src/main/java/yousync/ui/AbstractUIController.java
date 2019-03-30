package yousync.ui;

import org.springframework.beans.factory.annotation.Autowired;
import yousync.service.ConfigurationService;

public abstract class AbstractUIController {
    @Autowired
    ConfigurationService configurationService;
}