package com.rubyproducti9n.unofficialmech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InternshipData {


    private static final List<InternshipNotificationItem> panIndiaInternships = new ArrayList<>();
    private static final List<InternshipNotificationItem> internationalInternships = new ArrayList<>();

    static {
        panIndiaInternships.clear();
        panIndiaInternships.add(new InternshipNotificationItem("1",
                "https://miro.medium.com/v2/resize:fit:1400/1*YfZiGg60ii-3j9qHKhIYgA.jpeg",
                "TCS",
                "Pune",
                "https://www.tcs.com/careers/india/internship"));

        panIndiaInternships.add(new InternshipNotificationItem("1",
                "https://1000logos.net/wp-content/uploads/2021/12/Accenture-logo.jpg",
                "Accenture",
                "India",
                "https://www.accenture.com/ch-en/careers/life-at-accenture/internships-careers"));

        panIndiaInternships.add(new InternshipNotificationItem("1",
                "https://download.logo.wine/logo/Rolls-Royce_Holdings/Rolls-Royce_Holdings-Logo.wine.png",
                "Rolls Royce",
                "India",
                "https://careers.rolls-royce.com/germany/our-locations/india"));

        panIndiaInternships.add(new InternshipNotificationItem("1",
                "https://1000logos.net/wp-content/uploads/2020/04/Mahindra-Logo.jpg",
                "Mahindra",
                "India",
                "https://jobs.mahindracareers.com/"));

        panIndiaInternships.add(new InternshipNotificationItem("1",
                "https://static.vecteezy.com/system/resources/previews/028/339/965/original/microsoft-icon-logo-symbol-free-png.png",
                "Microsoft",
                "Hydrabad",
                "https://careers.microsoft.com/v2/global/en/locations/hyderabad.html"));

        panIndiaInternships.add(new InternshipNotificationItem("2",
                "https://static.vecteezy.com/system/resources/previews/014/018/561/non_2x/amazon-logo-on-transparent-background-free-vector.jpg",
                "Amazon",
                "PAN",
                "https://www.amazon.jobs/en"));

        panIndiaInternships.add(new InternshipNotificationItem("2",
                "https://upload.wikimedia.org/wikipedia/commons/f/fc/IBM_logo_in.jpg",
                "IBM",
                "",
                "https://www.ibm.com/in-en/careers/search?field_keyword_18[0]=Intern&field_keyword_18[1]=Entry%20Level&field_keyword_05[0]=India"));

        panIndiaInternships.add(new InternshipNotificationItem("2",
                "https://logowik.com/content/uploads/images/deloitte9811.jpg",
                "Deloitte",
                "",
                "null"));
        Collections.shuffle(panIndiaInternships);




        internationalInternships.clear();
        internationalInternships.add(new InternshipNotificationItem("2",
                "https://www.freepnglogos.com/uploads/google-logo-png/google-logo-icon-png-transparent-background-osteopathy-16.png",
                "Google",
                "",
                "https://www.google.com/about/careers/applications/dashboard"));

        internationalInternships.add(new InternshipNotificationItem("2",
                "https://download.logo.wine/logo/Rolls-Royce_Holdings/Rolls-Royce_Holdings-Logo.wine.png",
                "Rolls Royce",
                "Germany",
                "https://careers.rolls-royce.com/germany/our-locations/germany"));

        internationalInternships.add(new InternshipNotificationItem("2",
                "https://download.logo.wine/logo/Rolls-Royce_Holdings/Rolls-Royce_Holdings-Logo.wine.png",
                "Rolls Royce",
                "USA",
                "https://careers.rolls-royce.com/germany/our-locations/usa"));
        Collections.shuffle(internationalInternships);
    }

    public static List<InternshipNotificationItem> getPanIndiaInternships() {
        return panIndiaInternships;
    }

    public static List<InternshipNotificationItem> getInternationalInternships() {
        return internationalInternships;
    }

}
