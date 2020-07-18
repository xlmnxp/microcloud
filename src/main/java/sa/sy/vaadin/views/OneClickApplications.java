package sa.sy.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/oneclick.css")
public class OneClickApplications extends FlexLayout {
    public  OneClickApplications () {
        HorizontalLayout hl  = new HorizontalLayout();
        Button installButton = new Button("Install");
        installButton.setIcon(VaadinIcon.DOWNLOAD_ALT.create());
        Image img = new Image("https://res.cloudinary.com/canonical/image/fetch/f_auto,q_auto,fl_sanitize,w_60,h_60/https://dashboard.snapcraft.io/site_media/appmedia/2016/06/icon.svg_1.png", "next cloud");
        VerticalLayout labels = new VerticalLayout();
        labels.add(new Label("Nextcloud Server - A safe home for all your data\n"), new Pre("Where are your photos and documents? With Nextcloud you pick a server of your choice, at home, in a data center or at a provider. And that is where your files will be. Nextcloud runs on that server, protecting your data and giving you access from your desktop or mobile devices. Through Nextcloud you also access, sync and share your existing data on that FTP drive at school, a Dropbox or a NAS you have at home.\n"),
                installButton);
        hl.add(img, labels);
        labels.setPadding(false);
        labels.setMargin(false);
        hl.setPadding(false);
        hl.addClassName("oneclick-card");
        add(hl);
    }
}
