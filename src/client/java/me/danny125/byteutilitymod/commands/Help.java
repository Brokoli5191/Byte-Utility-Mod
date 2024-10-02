package me.danny125.byteutilitymod.commands;

import me.danny125.byteutilitymod.BYTE;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Help extends Command {
    public Help() {
        super("help","Generates list of commands","#help");
    }

    @Override
    public void runCommand(String msg) {
        super.runCommand(msg);
        if(MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("§5Command List:"));
            for(Command c : BYTE.commands){
                MinecraftClient.getInstance().player.sendMessage(Text.literal("§5" + c.getCommand() + " - " + c.getDescription()));
            }
        }
    }
}
