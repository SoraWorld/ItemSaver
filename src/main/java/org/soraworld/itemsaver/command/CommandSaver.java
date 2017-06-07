package org.soraworld.itemsaver.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import org.soraworld.itemsaver.constant.IMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.command.CommandBase.getPlayer;
import static net.minecraft.command.CommandBase.notifyCommandListener;
import static org.soraworld.itemsaver.ItemSaver.api;

public class CommandSaver implements ICommand {
    @Override
    public String getName() {
        return IMod.MODID;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "isv.help.usage";
    }

    @Override
    public List<String> getAliases() {
        List<String> alias = new ArrayList<>();
        alias.add("isv");
        return alias;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            switch (args[0]) {
                case "add":
                    throw new WrongUsageException("isv.help.add");
                case "give":
                    throw new WrongUsageException("isv.help.give");
                case "list":
                    for (String type : api.get().keySet()) {
                        ITextComponent _type = new TextComponentString(type).setStyle(IMod.YELLOW);
                        sender.sendMessage(new TextComponentTranslation("isv.list.type", IMod.PREFIX, _type));
                        HashMap<String, ItemStack> map = api.get(type);
                        for (String name : map.keySet()) {
                            ITextComponent _name = new TextComponentString(name).setStyle(IMod.RED);
                            ITextComponent hint = map.get(name).getTextComponent();
                            hint.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + type + " " + name));
                            sender.sendMessage(new TextComponentTranslation("isv.list.item", IMod.PREFIX, _name, hint));
                        }
                    }
                    break;
                case "save":
                    api.save();
                    sender.sendMessage(new TextComponentTranslation("isv.save", IMod.PREFIX));
                    break;
                case "reload":
                    api.reload();
                    sender.sendMessage(new TextComponentTranslation("isv.reload", IMod.PREFIX));
                    break;
                case "remove":
                    throw new WrongUsageException("isv.help.remove");
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "add":
                    throw new WrongUsageException("isv.help.add");
                case "give":
                    throw new WrongUsageException("isv.help.give");
                case "list":
                    HashMap<String, ItemStack> map = api.get(args[1]);
                    ITextComponent _type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                    sender.sendMessage(new TextComponentTranslation("isv.list.type", IMod.PREFIX, _type));
                    for (String name : map.keySet()) {
                        ITextComponent _name = new TextComponentString(name).setStyle(IMod.RED);
                        ITextComponent hint = map.get(name).getTextComponent();
                        hint.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + args[1] + " " + name));
                        sender.sendMessage(new TextComponentTranslation("isv.list.item", IMod.PREFIX, _name, hint));
                    }
                    break;
                case "save":
                    throw new WrongUsageException("isv.help.save");
                case "reload":
                    throw new WrongUsageException("isv.help.reload");
                case "remove":
                    api.remove(args[1]);
                    ITextComponent type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                    sender.sendMessage(new TextComponentTranslation("isv.type.remove", IMod.PREFIX, type));
                    break;
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length == 3) {
            switch (args[0]) {
                case "add":
                    if (sender instanceof EntityPlayerMP) {
                        ItemStack it = ((EntityPlayerMP) sender).getHeldItemMainhand();
                        if (it.getItem() != Items.AIR) {
                            api.add(args[1], args[2], it);
                            ITextComponent type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                            ITextComponent name = new TextComponentString(args[2]).setStyle(IMod.RED);
                            sender.sendMessage(new TextComponentTranslation("isv.name.add", IMod.PREFIX, type, name, it.getTextComponent()));
                        } else {
                            sender.sendMessage(new TextComponentTranslation("isv.help.null", IMod.PREFIX).setStyle(IMod.RED));
                        }
                    } else {
                        throw new WrongUsageException("isv.help.cmd");
                    }
                    break;
                case "give":
                    EntityPlayerMP target = getPlayer(server, sender, args[1]);
                    HashMap<String, ItemStack> map = api.get(args[2]);
                    for (String name : map.keySet()) {
                        ItemStack stack = map.get(name);
                        if (stack != null && stack.getItem() != Items.AIR) {
                            api.give(sender, target, stack, stack.getCount());
                        }
                    }
                    notifyCommandListener(sender, this, "commands.give.success", new TextComponentString("[" + args[2] + "]").setStyle(IMod.YELLOW), 1, target.getName());
                    break;
                case "list":
                    throw new WrongUsageException("isv.help.list");
                case "save":
                    throw new WrongUsageException("isv.help.save");
                case "reload":
                    throw new WrongUsageException("isv.help.reload");
                case "remove":
                    api.remove(args[1], args[2]);
                    ITextComponent type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                    ITextComponent name = new TextComponentString(args[2]).setStyle(IMod.RED);
                    sender.sendMessage(new TextComponentTranslation("isv.name.remove", IMod.PREFIX, type, name));
                    break;
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length >= 4 && args[0].equals("give")) {
            EntityPlayerMP target = getPlayer(server, sender, args[1]);
            ItemStack stack = api.get(args[2], args[3]);
            if (stack != null && stack.getItem() != Items.AIR) {
                int count = stack.getCount();
                if (args.length == 5 && args[4].matches("[0-9]{1,8}")) {
                    count = Integer.valueOf(args[4]);
                }
                api.give(sender, target, stack, count);
                ITextComponent hint = stack.getTextComponent();
                hint.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + args[2] + " " + args[3]));
                notifyCommandListener(sender, this, "commands.give.success", hint, count, target.getName());
            } else {
                throw new WrongUsageException("isv.help.null");
            }
        } else {
            throw new WrongUsageException("isv.help.usage");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args[0].equals("give") && index == 1;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
