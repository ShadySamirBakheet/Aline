package com.app.aline.views.home.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.aline.R
import com.app.aline.adapters.ArticleAdapter
import com.app.aline.adapters.DeptApadter
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.datasources.TempData
import com.app.aline.data.models.Article
import com.app.aline.data.models.ArticleDept
import com.app.aline.databinding.FragmentHomeBinding
import com.app.aline.viewmodel.ArticleViewModel
import com.app.aline.viewmodel.NetworkViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var articleViewModel: ArticleViewModel

    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articles: ArrayList<Article>

    var selectedDept = "";


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        articleViewModel = ViewModelProvider(this)[ArticleViewModel::class.java]

        articles = ArrayList()
        articleAdapter =
            ArticleAdapter(context, SharedStorage.getUserName(requireContext()).toString())

        articleAdapter.setOnItemDeleteListener {
            showDialogFun(it)
        }

        binding.trips.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = articleAdapter
        }

        getData()

        binding.inputSearch.doOnTextChanged { text, _, _, _ ->
            val new = ArrayList<Article>()
            for (article in articles) {
                if (!(!article.articleTitle
                            !!.lowercase()
                        .contains(text.toString().trim().lowercase()) && !article.article!!.
                    lowercase()
                        .contains(text.toString().trim().lowercase()))
                ) {
                    new.add(article)
                }
            }
            articleAdapter.addData(new)
            if (new.size <= 0) {

                articleAdapter.clearData()
                binding.empty.visibility = VISIBLE
            } else {
                binding.empty.visibility = GONE
            }
        }


        binding.filter.setOnClickListener {
            if (TempData.depts.size > 1) {
                filterDialog()
            } else {
                Toast.makeText(context, "Not found Depts", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun showDialogFun(it: Article) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Delete Article")
            .setMessage("Are you want to Delete ${it.articleTitle}")
            .setPositiveButton("Ok") { dialogInterface, _ ->
                deleteFun(it)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }

    private fun filterDialog() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.popup_dept, null)

        alertDialogBuilder.setView(dialogLayout)
        val alertDialog = alertDialogBuilder.create()

        val deptAdapter = DeptApadter(context, TempData.depts)


        dialogLayout.findViewById<RecyclerView>(R.id.depts).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = deptAdapter
        }

        deptAdapter.setOnItemClickListener {

               val s = articleAdapter.setView(it)
               if (s == 0) {

                   articleAdapter.clearData()
                   binding.empty.visibility = VISIBLE
               } else {
                   binding.empty.visibility = GONE
               }
               alertDialog.dismiss()

        }

        dialogLayout.findViewById<TextView>(R.id.cancel).setOnClickListener {
            //startActivity(Intent(this,GetLocationMapsActivity::class.java))
            val s = articleAdapter.setAll()
            if (s == 0) {
                articleAdapter.clearData()
                binding.empty.visibility = VISIBLE
            } else {
                binding.empty.visibility = GONE
            }
            alertDialog.dismiss()
        }

        alertDialog.show()

    }

    private fun deleteFun(article: Article) {
        networkViewModel.networkState(context).observe(viewLifecycleOwner) {
            if (it) {
                binding.progress.visibility = VISIBLE
                articleViewModel.deleteArticleData(article).addOnCompleteListener {
                    binding.progress.visibility = GONE
                }.addOnFailureListener {
                    binding.progress.visibility = GONE
                }
            } else {
                Toast.makeText(context, "Check Network is Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getData() {
        networkViewModel.networkState(context).observe(viewLifecycleOwner) {
            if (it) {
                binding.progress.visibility = VISIBLE
                getArticlesData()
            } else {

                Toast.makeText(context, "Check Network is Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getArticlesData() {
        articleViewModel.getArticlesData().observe(viewLifecycleOwner) { data ->
            TempData.depts.clear()

            binding.progress.visibility = GONE
            articles.clear()
            if (data != null) {
                if (data.hasChildren()) {
                    data.children.forEach { deptData ->
                        val articleDept = deptData.getValue(ArticleDept::class.java)
                        if (articleDept != null) {
                            TempData.depts.add(articleDept)
                        }
                        if (deptData.hasChild("articles")) {
                            if (deptData.child("articles").hasChildren()) {
                                deptData.child("articles").children.forEach {
                                    val article = it.getValue(Article::class.java)
                                    /*if (it.hasChild("images")){
                                        val images = ArrayList<ArticleImages>()
                                        it.children.forEach { image->
                                            val articleImages = image.getValue(ArticleImages::class.java)
                                            if (articleImages != null) {
                                                images.add(articleImages)
                                            }
                                        }
                                        if (article != null) {
                                            article.images=images
                                        }
                                    }*/
                                    if (article != null) {
                                        articles.add(article)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (articles.isNotEmpty()) {
                articleAdapter.addData(articles)
                binding.empty.visibility = GONE
            } else {

                articleAdapter.clearData()
                binding.empty.visibility = VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}