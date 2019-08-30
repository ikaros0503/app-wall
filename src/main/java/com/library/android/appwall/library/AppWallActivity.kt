package com.library.android.appwall.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.library.android.appwall.library.adapter.AppWallItemAdapter
import com.library.android.appwall.library.dependencies.ApiDependencies
import com.library.android.appwall.library.dependencies.LocalAppRepository
import com.library.android.appwall.library.viewmodel.AppWallViewModel
import kotlinx.android.synthetic.main.layout_app_wall.*

open class AppWallActivity : AppCompatActivity() {

    protected lateinit var itemAdapter: AppWallItemAdapter
    private lateinit var viewModel: AppWallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_app_wall)

        viewModel =
            ViewModelProvider(
                AppWall.getViewModelStore(),
                AppWallViewModel.Factory(
                    ApiDependencies(),
                    LocalAppRepository(this)
                )
            ).get(AppWallViewModel::class.java)

        itemAdapter = AppWallItemAdapter()

        toolbar_layout_appwall.setNavigationOnClickListener { finish() }

        recyclerview_list_app.layoutManager = LinearLayoutManager(this)
        recyclerview_list_app.adapter = itemAdapter
        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.getListApps().observe(this, Observer { data ->
            if (data != null) {
                itemAdapter.submitList(data)
            }
        })
    }
}