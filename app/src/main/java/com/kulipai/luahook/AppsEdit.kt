package com.kulipai.luahook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kulipai.luahook.adapter.ToolAdapter
import kotlin.system.exitProcess


class AppsEdit : AppCompatActivity() {

    //ui绑定区
    private val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    private val editor: LuaEditor by lazy { findViewById(R.id.editor) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }
    private val rootLayout: CoordinatorLayout by lazy { findViewById(R.id.main) }
    private val bottomSymbolBar: LinearLayout by lazy { findViewById(R.id.bottomBar) }


    //全局变量
    private lateinit var currentPackageName: String
    private lateinit var appName: String


    companion object {
        const val PREFS_NAME = "apps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apps_edit)
        setSupportActionBar(toolbar)



        //窗口处理
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())

            // 调整底部符号栏的位置，使其位于输入法上方
            bottomSymbolBar.translationY = -imeInsets.bottom.toFloat()
            fab.translationY = -imeInsets.bottom.toFloat()

            // 设置根布局的底部内边距
            if (imeInsets.bottom > 0) {
                // 输入法可见时，不需要额外的底部内边距来避免被导航栏遮挡，
                // 因为 bottomSymbolBar 已经移动到输入法上方
                view.setPadding(
                    navigationBarInsets.left,
                    statusBarInsets.top,
                    navigationBarInsets.right,
                    0
                )

            } else {
                // 输入法不可见时，设置底部内边距以避免内容被导航栏遮挡
                view.setPadding(
                    navigationBarInsets.left,
                    statusBarInsets.top,
                    navigationBarInsets.right,
                    navigationBarInsets.bottom
                )
            }

            insets
        }

        // 确保在布局稳定后请求 WindowInsets，以便监听器能够正确工作
        ViewCompat.requestApplyInsets(rootLayout)


        //接收传递信息
        val intent = getIntent()
        if (intent != null) {
            currentPackageName = intent.getStringExtra("packageName").toString()
            appName = intent.getStringExtra("appName").toString()
//            toolbar.title = appName
            title = appName


        }

        val symbols =
            listOf("hook", "lp", "(", ")", "\"", ":", "=", "[", "]", "{", "}", "+", "-", "?", "!")
        val symbolRecyclerView: RecyclerView = findViewById(R.id.symbolRecyclerView)
        symbolRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        symbolRecyclerView.adapter = ToolAdapter(symbols, editor)




        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val script = prefs.getString(currentPackageName, "")

        editor.setText(script)

        fab.setOnClickListener {
            savePrefs(currentPackageName, editor.text.toString())
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            softRestartApp()
        }


    }

    //菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Undo")
            ?.setIcon(R.drawable.undo_24px)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        menu?.add(0, 2, 0, "Redo")
            ?.setIcon(R.drawable.redo_24px)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        menu?.add(0, 3, 0,  resources.getString(R.string.format))
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu?.add(0, 4, 0, resources.getString(R.string.log))  //LogCat
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu?.add(0, 5, 0, resources.getString(R.string.manual))
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                // "Undo"
                editor.undo()
                true
            }

            2 -> {
                // "Redo"
                editor.redo()
                true
            }

            3 -> {
                // 格式化
                editor.format()
                true
            }

            4 -> {
                //LogCat
                val intent = Intent(this, LogCatActivity::class.java)
                startActivity(intent)
                true
            }

            5 -> {
                //示例
                val intent = Intent(this, Manual::class.java)
                startActivity(intent)
                true
            }


            else -> false
        }
    }



    // 写入 SharedPreferences 并修改权限
    fun savePrefs(packageName: String, text: String) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putString(packageName, text)
            apply()
        }
    }


    fun Context.softRestartApp(delayMillis: Long = 100) {
        //保存状态
        val prefs = getSharedPreferences("status", MODE_PRIVATE)
        prefs.edit {
            putString("current","apps")
            putString("packageName",currentPackageName)
            putString("appName",appName)
        }

        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            // 结束当前应用的所有 Activity
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }, delayMillis)
    }
}