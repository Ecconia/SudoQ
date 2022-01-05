/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.sudoq.R
import de.sudoq.controller.SudoqListActivity
import de.sudoq.controller.sudoku.SudokuActivity
import de.sudoq.model.game.GameData
import de.sudoq.model.game.GameManager
import de.sudoq.model.persistence.xml.game.IGamesListRepo
import de.sudoq.model.profile.ProfileManager
import de.sudoq.persistence.game.GameRepo
import de.sudoq.persistence.game.GamesListRepo
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo
import de.sudoq.persistence.sudokuType.SudokuTypeRepo
import java.io.*
import java.nio.charset.Charset
import java.util.*

/**
 * Diese Klasse repräsentiert den Lade-Controller des Sudokuspiels. Mithilfe von
 * SudokuLoading können Sudokus geladen werden und daraufhin zur SudokuActivity
 * gewechselt werden.
 */
class SudokuLoadingActivity : SudoqListActivity(), OnItemClickListener, OnItemLongClickListener {
    /** Attributes  */
    private var profileManager: ProfileManager? = null
    private var adapter: SudokuLoadingAdapter? = null
    private var games: List<GameData>? = null


    private lateinit var profilesDir: File
    private lateinit var sudokuDir: File
    private lateinit var sudokuTypeRepo: SudokuTypeRepo
    private lateinit var gameManager: GameManager

    /*	protected static MenuItem menuDeleteFinished;
	private static final int MENU_DELETE_FINISHED = 0;

	protected static MenuItem menuDeleteSpecific;
	private static final int MENU_DELETE_SPECIFIC = 1; commented out to make sure it's not needed*/
    private enum class FabStates {
        DELETE, INACTIVE, GO_BACK
    } //Floating Action Button

    private var fabState = FabStates.INACTIVE

    /**
     * Wird aufgerufen, wenn SudokuLoading nach Programmstart zum ersten Mal
     * geladen aufgerufen wird. Hier wird das Layout inflated und es werden
     * nötige Initialisierungen vorgenommen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        sudokuDir = getDir(getString(R.string.path_rel_sudokus), MODE_PRIVATE)
        sudokuTypeRepo = SudokuTypeRepo(sudokuDir)
        profileManager = ProfileManager(profilesDir, ProfileRepo(profilesDir),
                                        ProfilesListRepo(profilesDir))


        ///init params for game*repos
        profileManager!!.loadCurrentProfile()
        val gameRepo = GameRepo(
            profileManager!!.profilesDir!!,
            profileManager!!.currentProfileID,
            sudokuTypeRepo)
        val gamesFile = File(profileManager!!.currentProfileDir, "games.xml")

        val gamesDir = File(profileManager!!.currentProfileDir, "games")
        val gamesListRepo : IGamesListRepo = GamesListRepo(gamesDir, gamesFile)

        gameManager = GameManager(profileManager!!, gameRepo, gamesListRepo, sudokuTypeRepo)

        //needs to be called before setcontentview which calls onContentChanged
        check(!profileManager!!.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        profileManager!!.loadCurrentProfile()
        setContentView(R.layout.sudokuloading)

        //toolbar
        initToolBar()

        initFAB(this)

        initialiseGames()
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
    }

    private fun initFAB(ctx: Context) {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener {
            var trash = ContextCompat.getDrawable(ctx, R.drawable.ic_delete_white_24dp)
            var close = ContextCompat.getDrawable(ctx, R.drawable.ic_close_white_24dp)
            override fun onClick(view: View) {
                when (fabState) {
                    FabStates.INACTIVE -> {
                        // ...
                        fabState = FabStates.DELETE
                        fab.setImageDrawable(close)
                        Toast.makeText(ctx, R.string.fab_go_back, Toast.LENGTH_LONG).show()
                    }
                    FabStates.DELETE -> {
                        fabState = FabStates.INACTIVE
                        fab.setImageDrawable(trash)
                    }
                    FabStates.GO_BACK -> goBack(view)
                }
            }
        })
    }

    /// Action Bar

    /**
     * Wird beim ersten Anzeigen des Options-Menü von SudokuLoading aufgerufen
     * und initialisiert das Optionsmenü indem das Layout inflated wird.
     *
     * @return true falls das Options-Menü angezeigt werden kann, sonst false
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_sudoku_loading, menu)
        return true
    }

    /**
     * Wird beim Auswählen eines Menü-Items im Options-Menü aufgerufen. Ist das
     * spezifizierte MenuItem null oder ungültig, so wird nichts getan.
     *
     * @param item
     * Das ausgewählte Menü-Item
     * @return true, falls die Selection hier bearbeitet wird, false falls nicht
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sudokuloading_delete_finished -> {
                gameManager.deleteFinishedGames()
            }
            R.id.action_sudokuloading_delete_all -> {
                gameManager.gameList.forEach { gameManager.deleteGame(it.id) }
            }
            else -> super.onOptionsItemSelected(item)
        }
        onContentChanged()
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val noGames = gameManager.gameList.isEmpty()
        menu.findItem(R.id.action_sudokuloading_delete_finished).isVisible = !noGames
        menu.findItem(R.id.action_sudokuloading_delete_all).isVisible = !noGames
        return true
    }


    /**
     * {@inheritDoc}
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * {@inheritDoc}
     */
    override fun onContentChanged() {
        super.onContentChanged()
        initialiseGames()
        profileManager!!.currentGame = if (adapter!!.isEmpty) -1 else adapter!!.getItem(0)!!.id
    }

    /**
     * Wird aufgerufen, falls ein Element (eine View) in der AdapterView
     * angeklickt wird.
     *
     * @param parent
     * AdapterView in welcher die View etwas angeklickt wurde
     * @param view
     * View, welche angeklickt wurde
     * @param position
     * Position der angeklickten View im Adapter
     * @param id
     * ID der angeklickten View
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        Log.d(LOG_TAG, position.toString() + "")
        if (fabState == FabStates.INACTIVE) {
            /* selected in order to play */
            profileManager!!.currentGame = adapter!!.getItem(position)!!.id
            startActivity(Intent(this, SudokuActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            /*selected in order to delete*/
            gameManager.deleteGame(adapter!!.getItem(position)!!.id)
            onContentChanged()
        }
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        Log.d(LOG_TAG, "LongClick on $position")

        /*gather all options */
        val temp_items: MutableList<CharSequence> = ArrayList()
        val specialcase = false
        if (specialcase) {
        } else {
            temp_items.add(getString(R.string.sudokuloading_dialog_play))
            temp_items.add(getString(R.string.sudokuloading_dialog_delete))
            if (profileManager!!.appSettings.isDebugSet) {
                temp_items.add("export as text")
                temp_items.add("export as file")
            }
        }
        val builder = AlertDialog.Builder(this)
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir),
                                ProfilesListRepo(profilesDir))
        val gameRepo = GameRepo(pm.profilesDir!!, pm.currentProfileID, sudokuTypeRepo)
        builder.setItems(temp_items.toTypedArray()) { dialog, item ->
            val gameID = adapter!!.getItem(position)!!.id
            when (item) {
                0 -> { // play
                    profileManager!!.currentGame = gameID
                    val i = Intent(this@SudokuLoadingActivity, SudokuActivity::class.java)
                    startActivity(i)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                1 -> { // delete
                    gameManager.deleteGame(gameID)
                    onContentChanged()
                }
                2 -> {
                    val gameFile = gameRepo.getGameFile(gameID)
                    var str = "there was an error reading the file, sorry"
                    var fis: FileInputStream? = null
                    try {
                        fis = FileInputStream(gameFile)
                        val data = ByteArray(gameFile.length().toInt())
                        fis.read(data)
                        fis.close()
                        str = String(data, Charset.forName("UTF-8"))
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND //
                    //sendIntent.putExtra(Intent.EXTRA_FROM_STORAGE, gameFile);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, str)
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                }
                3 -> {
                    //already defined under 2
                    val gameFile = gameRepo.getGameFile(gameID)
                    /* we can only copy from 'files' subdir, so we have to move the file there first */
                    val tmpFile = File(filesDir, gameFile.name)
                    val `in`: InputStream
                    val out: OutputStream
                    try {
                        `in` = FileInputStream(gameFile)
                        out = FileOutputStream(tmpFile)
                        Utility.copyFileOnStreamLevel(`in`, out)
                        `in`.close()
                        out.flush()
                        out.close()
                    } catch (e: IOException) {
                        e.message?.let { Log.e(LOG_TAG, it) }
                        Log.e(LOG_TAG, "there seems to be an io exception")
                    } catch (e: FileNotFoundException) {
                        e.message?.let { Log.e(LOG_TAG, it) }
                        Log.e(LOG_TAG, "there seems to be a file not found exception")
                    }
                    Log.v("file-share", "tmpfile: " + tmpFile.absolutePath)
                    Log.v("file-share", "gamefile is null? " + (gameFile == null))
                    Log.v("file-share", "gamefile getPath " + gameFile.path)
                    Log.v("file-share", "gamefile getAbsolutePath " + gameFile.absolutePath)
                    Log.v("file-share", "gamefile getName " + gameFile.name)
                    Log.v("file-share", "gamefile getParent " + gameFile.parent)
                    val fileUri = FileProvider.getUriForFile(
                        this@SudokuLoadingActivity,
                        "de.sudoq.fileprovider", tmpFile
                    )
                    Log.v("file-share", "uri is null? " + (fileUri == null))
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND //
                    //sendIntent.putExtra(Intent.EXTRA_FROM_STORAGE, gameFile);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                    sendIntent.type = "text/plain"
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    //startActivity(Intent.createChooser(sendIntent, "Share to"));
                    startActivity(sendIntent)
                }
            }
        }
        val alert = builder.create()
        alert.show()
        return true //prevent itemclick from fire-ing as well
    }

    private fun initialiseGames() {
        games = gameManager.gameList
        // initialize ArrayAdapter for the profile names and set it
        adapter = SudokuLoadingAdapter(this, games!!)
        listAdapter = adapter
        listView!!.onItemClickListener = this
        listView!!.onItemLongClickListener = this
        val noGamesTextView = findViewById<TextView>(R.id.no_games_text_view)
        if (games!!.isEmpty()) {
            noGamesTextView.visibility = View.VISIBLE
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            fab.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_arrow_back_white_24dp
                )
            )
            fabState = FabStates.GO_BACK
        } else {
            noGamesTextView.visibility = View.INVISIBLE
            //pass
        }
    }

    /**
     * Führt die onBackPressed-Methode aus.
     *
     * @param view
     * unbenutzt
     */
    fun goBack(view: View?) {
        super.onBackPressed()
    }


    /**
     * Just for testing!
     * @return
     * number of saved games
     */
    val size: Int
        get() = games!!.size

    private inner class FAB(context: Context?) : FloatingActionButton(context) {
        private var fs: FabStates? = null
        fun setState(fs: FabStates?) {
            this.fs = fs
            val id: Int = when (fs) {
                FabStates.DELETE -> R.drawable.ic_close_white_24dp
                FabStates.INACTIVE -> R.drawable.ic_delete_white_24dp
                else -> R.drawable.ic_arrow_back_white_24dp
            }
            super.setImageDrawable(ContextCompat.getDrawable(this.context, id))
        }
    }

    companion object {
        /**
         * Der Log-Tag für das LogCat
         */
        private val LOG_TAG = SudokuLoadingActivity::class.java.simpleName
    }
}