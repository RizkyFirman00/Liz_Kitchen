package com.dissy.lizkitchen.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dissy.lizkitchen.databinding.FragmentMetodeAmbilBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MetodeAmbilFragment : BottomSheetDialogFragment() {

    interface MetodePengambilanListener {
        fun onMetodePengambilanSelected(metode: String)
    }

    private var listener: MetodePengambilanListener? = null

    fun setListener(listener: MetodePengambilanListener) {
        this.listener = listener
    }

    private val binding by lazy { FragmentMetodeAmbilBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.apply {
            btnPickup.setOnClickListener {
                listener?.onMetodePengambilanSelected("Ambil Sendiri")
                dismiss()
            }
            btnAntar.setOnClickListener {
                listener?.onMetodePengambilanSelected("Pesan Antar")
                dismiss()
            }
        }

        return binding.root
    }

}