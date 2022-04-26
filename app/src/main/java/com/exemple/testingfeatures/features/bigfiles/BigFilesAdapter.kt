package com.exemple.testingfeatures.features.bigfiles

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.ItemFileBinding
import com.exemple.testingfeatures.utils.ScanMemoryBoost
import com.exemple.testingfeatures.utils.asLiveData
import java.io.File

class BigFilesAdapter(
    private val files: List<File>
) : RecyclerView.Adapter<BigFilesAdapter.CustomViewHolder>() {

    private val mData: MutableList<RecyclerViewData> = ArrayList()
    var cleanedSize: Float = 0F
    private var _selectedCount = MutableLiveData(0)
    val selectedCount get() = _selectedCount.asLiveData()


    fun load(){
        for (i in files.indices){
            mData.add(
                RecyclerViewData(
                    R.drawable.ic_file,
                    files[i].name,
                    files[i],
                    false
                )
            )
        }
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding: ItemFileBinding by viewBinding(ItemFileBinding::bind)
        var mark: ImageView = viewBinding.listMark

        fun bind(file: RecyclerViewData) {
            viewBinding.ivIcon.setImageDrawable(file.imageDrawable)
            viewBinding.tvName.text = file.stringName
            viewBinding.tvSize.text = file.stringSize
            viewBinding.listSelect.setOnClickListener {
                val isSelect: Boolean = mData[absoluteAdapterPosition].isSelect
                mData[absoluteAdapterPosition].isSelect = !isSelect
                notifyItemChanged(absoluteAdapterPosition)
                if (!isSelect){
                    _selectedCount.value = _selectedCount.value!! + 1
                }  else{
                    _selectedCount.value = _selectedCount.value!! - 1
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return CustomViewHolder(cell)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val recyclerViewData: RecyclerViewData = mData[position]
        holder.bind(recyclerViewData)
        if (recyclerViewData.isSelect) holder.mark.visibility =
            View.VISIBLE else holder.mark.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return files.size
    }

    fun cleanSelected(context: Context) {
        for (rvd in mData) {
            Log.d(TAG, "cleanSelected: ${rvd.isSelect}")
            if (rvd.isSelect) {
                cleanedSize += ScanMemoryBoost(context).deleteRecursive(rvd.thisFile!!)
            }
        }
        Log.wtf(TAG, "Deleted size: $cleanedSize")
    }
}
