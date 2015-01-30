package za.co.johanmynhardt.jweatherhistory.gui;

import com.google.common.eventbus.EventBus;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import za.co.johanmynhardt.jweatherhistory.api.data.IDataExport;
import za.co.johanmynhardt.jweatherhistory.gui.events.ItemsUpdatedEvent;
import za.co.johanmynhardt.jweatherhistory.impl.config.AppConfig;

@Component
public class JWeatherHistoryUI implements ApplicationContextAware {
    private static Logger LOG = LoggerFactory.getLogger(JWeatherHistoryUI.class);
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        LOG.warn("Starting up...");

        MainFrame mainFrame = context.getBean(MainFrame.class);
        mainFrame.setVisible(true);

        context.getBean(EventBus.class).post(new ItemsUpdatedEvent() {
        });
    }

    @PreDestroy
    public void destroy() {
        LOG.warn("----------------------------------------");
        LOG.warn("Shutting down at {}", new Date());

    }

    public static void main(String[] args) {

        LOG.warn("----------------------------------------");
        LOG.warn("Starting at {}", new Date());
        LOG.warn("Launching JWeatherHistoryUI...");

        try {
            PlasticLookAndFeel plasticLookAndFeel = new PlasticLookAndFeel();
            PlasticLookAndFeel.setCurrentTheme(new LightGray());
            UIManager.setLookAndFeel(plasticLookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ((ConfigurableApplicationContext) context).registerShutdownHook();
        context.getBean(JWeatherHistoryUI.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        LOG.debug("applicationContext={}", applicationContext);
    }
}
