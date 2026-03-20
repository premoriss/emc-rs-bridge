package com.enesbilgin.emcrsbridge.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ProjectE 1.12.2 API farklarına karşı daha toleranslı olmak için reflection kullanır.
 * Gerekirse tek düzenlenecek yer burasıdır.
 */
public final class ProjectEReflection {
    private static Class<?> projecteApiClass;
    private static Method getEmcProxyMethod;
    private static Method getValueMethod;
    private static Field knowledgeCapabilityField;

    private ProjectEReflection() {
    }

    private static void bootstrap() {
        if (projecteApiClass != null) {
            return;
        }
        try {
            projecteApiClass = Class.forName("moze_intel.projecte.api.ProjectEAPI");
            getEmcProxyMethod = projecteApiClass.getMethod("getEMCProxy");
            knowledgeCapabilityField = projecteApiClass.getField("KNOWLEDGE_CAPABILITY");
        } catch (Exception ignored) {
        }
    }

    public static long getItemEmc(ItemStack stack) {
        bootstrap();
        if (projecteApiClass == null || stack.isEmpty()) {
            return 0L;
        }
        try {
            Object emcProxy = getEmcProxyMethod.invoke(null);
            if (emcProxy == null) {
                return 0L;
            }

            if (getValueMethod == null) {
                try {
                    getValueMethod = emcProxy.getClass().getMethod("getValue", ItemStack.class);
                } catch (NoSuchMethodException ex) {
                    getValueMethod = emcProxy.getClass().getMethod("getValueOf", ItemStack.class);
                }
            }

            Object result = getValueMethod.invoke(emcProxy, stack);
            return result instanceof Number ? ((Number) result).longValue() : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long getPlayerEmc(EntityPlayer player) {
        Object provider = getKnowledgeProvider(player);
        if (provider == null) {
            return 0L;
        }
        try {
            Method m;
            try {
                m = provider.getClass().getMethod("getEmc");
            } catch (NoSuchMethodException e) {
                m = provider.getClass().getMethod("getEMC");
            }
            Object result = m.invoke(provider);
            return result instanceof Number ? ((Number) result).longValue() : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static boolean consumePlayerEmc(EntityPlayer player, long amount) {
        Object provider = getKnowledgeProvider(player);
        if (provider == null) {
            return false;
        }

        long before = getPlayerEmc(player);
        if (before < amount) {
            return false;
        }

        try {
            Method method;
            try {
                method = provider.getClass().getMethod("removeEmc", long.class);
            } catch (NoSuchMethodException e1) {
                try {
                    method = provider.getClass().getMethod("removeEMC", long.class);
                } catch (NoSuchMethodException e2) {
                    method = provider.getClass().getMethod("setEmc", long.class);
                    method.invoke(provider, before - amount);
                    sync(provider);
                    return true;
                }
            }
            method.invoke(provider, amount);
            sync(provider);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean playerKnows(EntityPlayer player, ItemStack stack) {
        Object provider = getKnowledgeProvider(player);
        if (provider == null || stack.isEmpty()) {
            return false;
        }

        // Önce API'de knowledge kontrolü var mı diye bak.
        for (String name : new String[]{"hasKnowledge", "knows", "hasFullKnowledge"}) {
            try {
                Method m = provider.getClass().getMethod(name, ItemStack.class);
                Object result = m.invoke(provider, stack);
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (Exception ignored) {
            }
        }

        // Bazı sürümlerde tam transmutation check yerine EMC değeri ve öğrenilmiş varsayımı kullanılabilir.
        return getItemEmc(stack) > 0L;
    }

    private static Object getKnowledgeProvider(EntityPlayer player) {
        bootstrap();
        if (projecteApiClass == null || player == null) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Capability<Object> cap = (Capability<Object>) knowledgeCapabilityField.get(null);
            return player.hasCapability(cap, null) ? player.getCapability(cap, null) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void sync(Object provider) {
        for (String name : new String[]{"sync", "syncEmc", "syncKnowledge"}) {
            try {
                Method m = provider.getClass().getMethod(name);
                m.invoke(provider);
                return;
            } catch (Exception ignored) {
            }
        }
    }
}
