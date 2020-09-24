package org.asamk.signal.commands;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.asamk.Signal;
import org.asamk.signal.util.GroupIdFormatException;
import org.asamk.signal.util.IOUtils;
import org.asamk.signal.util.Util;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.push.ContactTokenDetails;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.asamk.signal.util.ErrorUtils.handleAssertionError;
import static org.asamk.signal.util.ErrorUtils.handleGroupIdFormatException;

public class GetUsernameCommand implements DbusCommand {

    @Override
    public void attachToSubparser(final Subparser subparser) {
        subparser.addArgument("contact")
                .help("Specify the contact's phone number.");
        subparser.addArgument("-e", "--endsession")
                .help("Clear session state and send end session message.")
                .action(Arguments.storeTrue());
    }

    @Override
    public int handleCommand(final Namespace ns, final Signal signal) {
        if (!signal.isRegistered()) {
            System.err.println("User is not registered.");
            return 1;
        }

        if ((ns.get("contact") == null)) {
            System.err.println("No contact given");
            System.err.println("Aborting sending.");
            return 1;
        }

        if (ns.getBoolean("endsession")) {
            try {
                signal.sendEndSessionMessage(ns.getList("recipient"));
                return 0;
            } catch (AssertionError e) {
                handleAssertionError(e);
                return 1;
            } catch (DBusExecutionException e) {
                System.err.println("Failed to send message: " + e.getMessage());
                return 1;
            }
        }

        try {
            String number = ns.get("contact");
            Optional<ContactTokenDetails> contactInfo = signal.getContactInfo(number);
            if (!contactInfo.isPresent()) {
                System.out.println("unknown");
            } else {
                ContactTokenDetails details = contactInfo.get();
                System.out.println("registered");
            }
            return 0;
        } catch (AssertionError e) {
            handleAssertionError(e);
            return 1;
        } catch (DBusExecutionException | IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            return 1;
        }
    }
}
