package de.sudoq.persistence.game

import de.sudoq.model.game.GameSettings
import de.sudoq.persistence.sudoku.sudokuTypes.SudokuTypesListBE

object GameSettingsMapper {

    fun toBE(gs: GameSettings): GameSettingsBE {
        return GameSettingsBE(
            gs.assistances,
            gs.isLefthandModeSet,
            gs.isHelperSet,
            gs.isGesturesSet,
            SudokuTypesListBE(gs.wantedTypesList)
        )
    }

    fun fromBE(gs: GameSettingsBE): GameSettings {
        return GameSettings(
            gs.assistances,
            gs.isLefthandModeSet,
            gs.isHelperSet,
            gs.isGesturesSet,
            gs.wantedTypesList)
    }

}