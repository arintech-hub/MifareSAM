/*
 * The content of this project itself is licensed under the Creative Commons 
 * Attribution 3.0 Unported license, and the underlying source code used to 
 * format and display that content is licensed under the GNU License
 */
package SAM_Lesson;

import java.util.List;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.*;

/**
 * @author Tere Varano 
 * @version 2.0
 * @since 2021/06/10
 */

// A collection of methods to communicate to authenticate a Mifare DESFire Key with Mifare SAM AV2
public class AuthenExample {
    
    public static void main(String[] args) throws Exception {
        
        // Prepare the comms channels
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        System.out.println("Terminals: " + terminals);
        
        CardChannel channel_4Card = createCardChannel(0);
        CardChannel channel_4SAM = createCardChannel(1);
        
        try{
            ResponseAPDU response;
            
             // Select Jalisco Application
            response = channel_4Card.transmit(new CommandAPDU(new byte[]{(byte)0x90, 0x5A, 0x00, 0x00, 0x03, 0x48, 0x40, 0x00, 0x00}));
            log("Response Select App :"+ toHexString(response.getBytes()));        
            // Get files IDs
            response = channel_4Card.transmit(new CommandAPDU(new byte[]{(byte)0x90, 0x6F, 0x00, 0x00, 0x00}));
            log("Response Get Files:" + toHexString(response.getBytes()));
            
            /********* Authenticate key #1 with SAM *********/
            
            // Ask PICC for authentication challenge 
            byte[] toCard_command_1 = new byte[]{(byte)0x90, (byte)0xAA, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x00};
            response = channel_4Card.transmit(new CommandAPDU(toCard_command_1));  
            log("Response challenge from PICC:" + toHexString(response.getBytes()));
            
            byte[] challenge_N1 = response.getData();
            
            // Send authentication challenge to SAM
            byte[] toSAM_command_header1 = new byte[]{(byte)0x80, (byte)0x0A, (byte)0x00, (byte)0x00, (byte)0x12, (byte)0x04, (byte)0x00};
            byte[] toSAM_command_butt1 = new byte[]{(byte)0x00};
            byte[] toSAM_command_1_pre = concatenate(toSAM_command_header1,challenge_N1);
            byte[] toSAM_command_1 = concatenate(toSAM_command_1_pre,toSAM_command_butt1);
               
             
            response = channel_4SAM.transmit(new CommandAPDU(toSAM_command_1));
            log("Response challenge from SAM:" + toHexString(response.getBytes()));
            
            byte[] challenge_N2 = response.getData();
            
            // Send authentication challenge to PICC
            byte[] toCard_command_header2 = new byte[]{(byte)0x90, (byte)0xAF, (byte)0x00, (byte)0x00, (byte)0x20}; 
            byte[] toCard_command_butt2 = new byte[]{(byte)0x00};
            byte[] toCard_command_2_pre = concatenate(toCard_command_header2,challenge_N2);
            byte[] toCard_command_2 = concatenate(toCard_command_2_pre,toCard_command_butt2);
                     
            response = channel_4Card.transmit(new CommandAPDU(toCard_command_2));
            log("Response challenge from PICC:" + toHexString(response.getBytes()));
            
            byte[] challenge_N3 = response.getData();
            
            // Send authentication challenge to PICC
            byte[] toSAM_command_header3 = new byte[]{(byte)0x80, (byte)0x0A, (byte)0x00, (byte)0x00, (byte)0x10}; 
            byte[] toSAM_command_3 = concatenate(toSAM_command_header3,challenge_N3);
                     
            response = channel_4SAM.transmit(new CommandAPDU(toSAM_command_3));
            log("Response challenge from SAM:" + toHexString(response.getBytes()));
            
        }finally{
            channel_4Card.getCard().disconnect(false);
            channel_4SAM.getCard().disconnect(false);
        }
    }

    private static CardChannel createCardChannel(int number) throws CardException {

        log("Opening channel...");

        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();

        log("Terminals: " + terminals);

        CardTerminal terminal = terminals.get(number);

        Card card = terminal.connect("T=1");

        byte[] atr = card.getATR().getBytes();
        log("ATR: " + toHexString(atr));

        return card.getBasicChannel();
    }

    private static void log(String msg) {
        System.out.println(msg);
    }


    private static String toHexString(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (byte item : data) {
            String hex = String.format("%02x", item);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] concatenate(byte[] dataA, byte[] dataB) {
        byte[] concatenated = new byte[dataA.length + dataB.length];

        for (int i = 0; i < dataA.length; i++) {
            concatenated[i] = dataA[i];
        }

        for (int i = 0; i < dataB.length; i++) {
            concatenated[dataA.length + i] = dataB[i];
        }

        return concatenated;
    }
}
    

