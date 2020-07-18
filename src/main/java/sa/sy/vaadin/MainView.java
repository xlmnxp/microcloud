package sa.sy.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.router.Route;
import sa.sy.lxd.Image;
import sa.sy.lxd.Instance;
import sa.sy.lxd.Lxd;
import sa.sy.vaadin.views.OneClickApplications;

import java.io.IOException;

/**
 * The main view contains a button and a click listener.
 */
@Route
@PWA(name = "Advance LXD Manager", shortName = "ALXDM")
@CssImport("./styles/main.css")
public class MainView extends HorizontalLayout {
    private final Lxd client;
    public MainView () throws Exception {
        client = new Lxd();
        setPadding(false);

        // Layouts
        VerticalLayout instancesView = new VerticalLayout();
        FlexLayout oneClickView = new OneClickApplications();
        HorizontalLayout toolbar = new HorizontalLayout();
        HorizontalLayout creationButtons = new HorizontalLayout();
        HorizontalLayout machineActions = new HorizontalLayout();
        instancesView.setWidthFull();
        oneClickView.setVisible(false);

        // Tabs
        // Instances
        Label instanceLbl = new Label("Instances");
        instanceLbl.setClassName("cursor-pointer");
        Tab instancesTab = new Tab(new Icon(VaadinIcon.SERVER), instanceLbl);

        // Settings
        Label settingsLbl = new Label("Settings");
        settingsLbl.setClassName("cursor-pointer");
        Tab settingsTab = new Tab(new Icon(VaadinIcon.COG), settingsLbl);

        // Volumes
        Label volumesLbl = new Label("Volumes");
        volumesLbl.setClassName("cursor-pointer");
        Tab volumesTab = new Tab(new Icon(VaadinIcon.HARDDRIVE), volumesLbl);

        // Networks
        Label networksLbl = new Label("Networking");
        networksLbl.setClassName("cursor-pointer");
        Tab networkingTab = new Tab(new Icon(VaadinIcon.SITEMAP), networksLbl);

        // One Click
        Label oneClickLbl = new Label("One Click");
        oneClickLbl.setClassName("cursor-pointer");
        Tab oneClickTab = new Tab(new Icon(VaadinIcon.POINTER), oneClickLbl);

        Tabs tabs = new Tabs(instancesTab, volumesTab, networkingTab, oneClickTab, settingsTab);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setFlexGrowForEnclosedTabs(1);
        tabs.addSelectedChangeListener(t -> {
            instancesView.setVisible(false);
            oneClickView.setVisible(false);
            if(tabs.getSelectedTab().equals(instancesTab)) {
                instancesView.setVisible(true);
            } if (tabs.getSelectedTab().equals(oneClickTab)) {
                oneClickView.setVisible(true);
            }
        });


        // Grid
        Grid<Instance> grid = new Grid<>(Instance.class);
        grid.setItems(client.getInstances());
        grid.setWidthFull();
        grid.setHeightByRows(true);
        grid.setColumns();
        grid.addColumn(Instance::getName).setHeader("Name").setSortable(true);
        grid.addComponentColumn(instance -> {
            Button btn = new Button(instance.getStatus());
            btn.addClickListener(t -> btn.setEnabled(true));
            btn.setDisableOnClick(true);
            if(instance.getStatus().equals("STOPPED")) {
                btn.setIcon(VaadinIcon.STOP.create());
                btn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            } else if (instance.getStatus().equals("RUNNING")){
                btn.setIcon(VaadinIcon.PLAY.create());
                btn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            }

            return btn;
        }).setHeader("Status").setSortable(true);

        grid.addComponentColumn(instance -> {
            Label lbl;
            try {
                lbl = new Label(instance.getIPv4());
            } catch (Exception e) {
                lbl = new Label("⸻");
            }

            return lbl;
        }).setHeader("IPv4").setSortable(true);

        grid.addComponentColumn(instance -> {
            Label lbl;
            try {
                lbl = new Label(instance.getIPv6());
            } catch (Exception e) {
                lbl = new Label("⸻");
            }

            return lbl;
        }).setHeader("IPv6").setSortable(true);
        grid.addComponentColumn(instance -> {
            Button btn = new Button(instance.getType().replace("-", " "));
            btn.addClickListener(t -> btn.setEnabled(true));
            btn.setDisableOnClick(true);
            btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            if(instance.getType().equals("virtual-machine")) {
                btn.setIcon(VaadinIcon.SERVER.create());
            } else if (instance.getType().equals("container")){
                btn.setIcon(VaadinIcon.PACKAGE.create());
            }
            return btn;
        }).setHeader("Type").setSortable(true);
//        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        // Action buttons
        Button refreshBtn = new Button("Refresh");
        Button stopBtn = new Button(VaadinIcon.STOP.create());
        Button startBtn = new Button(VaadinIcon.PLAY.create());
        Button configurationBtn = new Button("Configurations");
        refreshBtn.setIcon(VaadinIcon.REFRESH.create());
        stopBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        stopBtn.getElement().setAttribute("title", "stop");
        startBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        startBtn.getElement().setAttribute("title", "start");
        configurationBtn.setIcon(VaadinIcon.COG.create());
        stopBtn.setEnabled(false);
        startBtn.setEnabled(false);
        configurationBtn.setEnabled(false);
        machineActions.add(refreshBtn, startBtn, stopBtn, configurationBtn);

        // Creation buttons
        Button cContainerBtn = new Button("Create Container");
        Button cVirtualMachineBtn = new Button("Create Virtual Machine");
        cContainerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cContainerBtn.setIcon(VaadinIcon.PACKAGE.create());
        cVirtualMachineBtn.setIcon(VaadinIcon.SERVER.create());
        creationButtons.add(cContainerBtn, cVirtualMachineBtn);

        // Layouts
        toolbar.add(machineActions, creationButtons);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
        toolbar.setPadding(false);
        toolbar.setSpacing(false);
        toolbar.setMargin(false);
        instancesView.add(toolbar, grid);

        // Dialog
        Dialog creationDialog = new Dialog();
        FormLayout fl = new FormLayout();
        TextField instanceNameField = new TextField();
        Select<Image> imageFingerprintSelect = new Select<>();
        imageFingerprintSelect.setPlaceholder("Image fingerprint");
        imageFingerprintSelect.setTextRenderer(image -> String.format("%s %s %s", image.getFingerprint().substring(0, 8), image.getProperties().getString("os"), image.getProperties().getString("release")));
        Button save = new Button("Create");
        fl.addFormItem(instanceNameField, "Name");
        fl.addFormItem(imageFingerprintSelect, "Image");
        fl.setSizeFull();
        fl.add(save);
        save.addClickListener(t -> {
            String instanceName = instanceNameField.getValue();
            Image image = imageFingerprintSelect.getValue();
            instanceNameField.clear();
            imageFingerprintSelect.clear();

            try {
                client.createInstance(instanceName, image.getType() == Image.Type.Container ? Instance.Type.Container : Instance.Type.VirtualMachine, image.getFingerprint());
                refreshBtn.click();
                creationDialog.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        creationDialog.add(fl);

        // Events
        cContainerBtn.addClickListener(t -> {
            try {
                instanceNameField.clear();
                imageFingerprintSelect.clear();
                imageFingerprintSelect.setItems(client.getImages(Image.Type.Container));
                creationDialog.open();
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        cVirtualMachineBtn.addClickListener(to -> {
            try {
                instanceNameField.clear();
                imageFingerprintSelect.clear();
                imageFingerprintSelect.setItems(client.getImages(Image.Type.VirtualMachine));
                creationDialog.open();
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        refreshBtn.addClickListener(t -> {
            try {
                grid.setItems(client.getInstances());
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        startBtn.addClickListener((t) -> {
            try {
                Instance selectedInstance = (Instance) grid.getSelectedItems().toArray()[0];
                selectedInstance.setState(Instance.State.START);
                refreshBtn.click();
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        stopBtn.addClickListener((t) -> {
            try {
                Instance selectedInstance = (Instance) grid.getSelectedItems().toArray()[0];
                selectedInstance.setState(Instance.State.STOP);
                refreshBtn.click();
            } catch (Exception e) {
                Notification.show(e.getMessage());
            }
        });

        grid.addSelectionListener(t -> {
            if(grid.getSelectedItems().toArray().length > 0) {
                stopBtn.setEnabled(true);
                startBtn.setEnabled(true);
                configurationBtn.setEnabled(true);
            } else {
                stopBtn.setEnabled(false);
                startBtn.setEnabled(false);
                configurationBtn.setEnabled(false);
            }
        });

        add(tabs, instancesView, oneClickView);
    }
}

