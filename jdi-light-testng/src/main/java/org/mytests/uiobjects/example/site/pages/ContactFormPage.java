package org.mytests.uiobjects.example.site.pages;

import com.epam.jdi.light.elements.composite.WebPage;
import com.epam.jdi.light.elements.pageobjects.annotations.Title;
import com.epam.jdi.light.elements.pageobjects.annotations.Url;
import com.epam.jdi.light.elements.pageobjects.annotations.locators.UI;
import org.mytests.uiobjects.example.site.sections.ContactForm;

@Url("/contacts.html") @Title("Contact Form")
public class ContactFormPage extends WebPage {
	@UI("form#contact-form") public static ContactForm contactForm;
}