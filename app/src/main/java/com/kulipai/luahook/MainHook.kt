package com.kulipai.luahook


import HookLib
import LuaDrawableLoader
import LuaHttp
import LuaImport
import LuaJson
import LuaResourceBridge
import Luafile
import com.kulipai.luahook.util.d
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import org.luckypray.dexkit.DexKitBridge
import java.io.File


class MainHook : IXposedHookZygoteInit, IXposedHookLoadPackage {

    companion object {
        init {
            System.loadLibrary("dexkit")
        }

        const val MODULE_PACKAGE = "com.kulipai.luahook"  // 模块包名
        const val PREFS_NAME = "xposed_prefs"
        const val APPS = "apps"
    }


    lateinit var luaScript: String
    lateinit var appsScript: String
    lateinit var SelectAppsString: String
    lateinit var apps: XSharedPreferences
    lateinit var selectApps: XSharedPreferences
    lateinit var SelectAppsList: MutableList<String>

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {

        val pref = XSharedPreferences(MODULE_PACKAGE, PREFS_NAME)
        apps = XSharedPreferences(MODULE_PACKAGE, APPS)
        pref.makeWorldReadable()
        apps.makeWorldReadable()

        selectApps = XSharedPreferences(MODULE_PACKAGE, "MyAppPrefs")
        selectApps.makeWorldReadable()

        luaScript = pref.getString("lua", "nil").toString()


    }


    private fun canHook(lpparam: LoadPackageParam) {
        if (lpparam.packageName == MODULE_PACKAGE) {
            XposedHelpers.findAndHookMethod(
                "com.kulipai.luahook.fragment.HomeFragment",
                lpparam.classLoader,
                "canHook",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {

                    }

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        if (param != null) {
                            param.result = true
                        }
                    }
                }
            )
        }
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {


        SelectAppsString = selectApps.getString("selectApps", "").toString()

        if (SelectAppsString.isNotEmpty()) {
            SelectAppsList = SelectAppsString.split(",").toMutableList()
        } else {
            SelectAppsList = mutableListOf()
        }

        canHook(lpparam)


        val globals: Globals = JsePlatform.standardGlobals()


        //加载Lua模块
        globals["XposedHelpers"] = CoerceJavaToLua.coerce(XposedHelpers::class.java)
        globals["XposedBridge"] = CoerceJavaToLua.coerce(XposedBridge::class.java)
        globals["DexKitBridge"] = CoerceJavaToLua.coerce(DexKitBridge::class.java)
        globals["this"] = CoerceJavaToLua.coerce(this)
        HookLib(lpparam).call(globals)
        LuaJson().call(globals)
        LuaHttp().call(globals)
        Luafile().call(globals)
        globals["import"] = LuaImport(lpparam.classLoader, globals)


        val LuaFile = object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val path = arg.checkjstring()
                val file = File(path)
                return CoerceJavaToLua.coerce(file)
            }
        }

        globals["File"] = LuaFile

        LuaResourceBridge().registerTo(globals)

        LuaDrawableLoader().registerTo(globals)

        //全局脚本
        try {
            //排除自己
            if (lpparam.packageName != MODULE_PACKAGE) {
                val chunk: LuaValue = globals.load(luaScript)
                chunk.call()
            }
        } catch (e: Exception) {
            e.toString().d()
        }


        //app单独脚本
        try {
            if (lpparam.packageName in SelectAppsList) {
                appsScript = apps.getString(lpparam.packageName, "nil").toString()
                globals.load(appsScript).call()
            }
        } catch (e: Exception) {
            e.toString().d()
        }


    }


}