package org.asamk.signal.commands;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.asamk.signal.manager.Manager;
import org.asamk.signal.storage.SignalAccount;
import org.whispersystems.signalservice.api.push.exceptions.CaptchaRequiredException;

import java.io.IOException;

public class RegisterCommand implements LocalCommand {

    @Override
    public void attachToSubparser(final Subparser subparser) {
        subparser.addArgument("-v", "--voice")
                .help("The verification should be done over voice, not sms.")
                .action(Arguments.storeTrue());
    }

    @Override
    public int handleCommand(final Namespace ns, final Manager m) {
        try {
            m.register(ns.getBoolean("voice"));
            SignalAccount account = m.getAccount();
            System.out.println(String.format("Password: '%s'", account.getPassword()));
            return 0;
        } catch (CaptchaRequiredException e) {
            System.err.println("Captcha required for verification (" + e.getMessage() + ")");
            return 1;
        } catch (IOException e) {
            System.err.println("Request verify error: " + e.getMessage());
            return 3;
        }
    }
}
