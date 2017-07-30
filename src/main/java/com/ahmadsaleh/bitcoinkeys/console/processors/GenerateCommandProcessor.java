package com.ahmadsaleh.bitcoinkeys.console.processors;

import com.ahmadsaleh.bitcoinkeys.ECDSAEncryptionUtils;
import com.ahmadsaleh.bitcoinkeys.console.CommandOption;
import com.ahmadsaleh.bitcoinkeys.console.CommandProcessor;
import com.ahmadsaleh.bitcoinkeys.writer.BitcoinAddressWriter;
import com.ahmadsaleh.bitcoinkeys.writer.WalletImportFormatWriter;
import net.bither.bitherj.crypto.bip38.Bip38;
import net.bither.bitherj.exception.AddressFormatException;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class GenerateCommandProcessor implements CommandProcessor {

    @Override
    public void process(List<CommandOption> options) {
        try {
            KeyPair keyPair = ECDSAEncryptionUtils.generateKeyPair();
            String bitcoinAddress = toBitcoinAddress(keyPair.getPublic());
            String wifPrivateKey = toWalletImportFormat(keyPair.getPrivate());
            System.out.printf("bitcoin address: %s\n", bitcoinAddress);
            System.out.printf("private key (WIF): %s\n", wifPrivateKey);
            System.out.printf("encrypted private: %s\n", Bip38.encryptNoEcMultiply("koko", wifPrivateKey));
        } catch (IOException e) {
            throw new IllegalStateException("Error while converting keys", e);
        } catch (InterruptedException | AddressFormatException e) {
            throw new IllegalStateException("Error while encrypting keys", e);
        }
    }

    private String toBitcoinAddress(PublicKey publicKey) throws IOException {
        try (StringWriter stringWriter = new StringWriter();
             BitcoinAddressWriter addressWriter = new BitcoinAddressWriter(stringWriter);) {
            addressWriter.write(publicKey);
            addressWriter.flush();
            return stringWriter.toString();
        }
    }

    private String toWalletImportFormat(PrivateKey privateKey) throws IOException {
        try (StringWriter stringWriter = new StringWriter();
             WalletImportFormatWriter wifWriter = new WalletImportFormatWriter(stringWriter);) {
            wifWriter.write(privateKey);
            wifWriter.flush();
            return stringWriter.toString();
        }
    }

    @Override
    public String getCommand() {
        return "gen";
    }
}