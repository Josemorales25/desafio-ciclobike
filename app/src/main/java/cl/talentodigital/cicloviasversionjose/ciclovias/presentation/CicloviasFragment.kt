package cl.talentodigital.cicloviasversionjose.ciclovias.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cl.talentodigital.cicloviasversionjose.ciclovias.data.local.FakeRepository.CicloviasLocalRepository
import cl.talentodigital.cicloviasversionjose.ciclovias.domain.model.Ciclovia
import cl.talentodigital.cicloviasversionjose.ciclovias.domain.CicloviasRepository
import cl.talentodigital.cicloviasversionjose.ciclovias.domain.FiltroLasCondesUseCase
import cl.talentodigital.cicloviasversionjose.ciclovias.domain.ObtenerCicloviasUseCase
import cl.talentodigital.cicloviasversionjose.R
import cl.talentodigital.cicloviasversionjose.databinding.FragmentCicloviasBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class CicloviasFragment : Fragment(R.layout.fragment_ciclovias) {

    private lateinit var binding: FragmentCicloviasBinding
    private lateinit var cicloviasAdapter: CicloviasAdapter
    private lateinit var filtroLasCondesUseCase: FiltroLasCondesUseCase
    private lateinit var obtenerCicloviasUseCase: ObtenerCicloviasUseCase
    private lateinit var repository: CicloviasRepository
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDependencies()
        callUseCaseObtener()
        bindView(view)
        setupRecivlerView()
        setupListeners()
    }

    private fun setupDependencies() {
        repository = CicloviasLocalRepository()
        filtroLasCondesUseCase = FiltroLasCondesUseCase(repository)
        obtenerCicloviasUseCase = ObtenerCicloviasUseCase(repository)
    }

    private fun callUseCaseLasCondes() {
        compositeDisposable.add(filtroLasCondesUseCase.excecute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> handleReult(result) },
                { error -> handleError(error) }
            )
        )
    }

    private fun callUseCaseObtener() {
        compositeDisposable.add(obtenerCicloviasUseCase.excecute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> handleReult(result) },
                { error -> handleError(error) }
            )
        )
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(
            requireContext(),
            "Error {${error.message}}",
            Toast.LENGTH_LONG
        )
            .show()
    }

    private fun handleReult(result: List<Ciclovia>) {
        cicloviasAdapter = CicloviasAdapter(result)
        binding.rvCiclovias.adapter = cicloviasAdapter
    }

    private fun bindView(view: View) {
        binding = FragmentCicloviasBinding.bind(view)
    }

    private fun setupRecivlerView() {
        binding.apply {
            rvCiclovias.setHasFixedSize(true)
            rvCiclovias.layoutManager = LinearLayoutManager(
                requireContext()
            )
            rvCiclovias.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnFiltroLasCondes.setOnClickListener {
                callUseCaseLasCondes()
            }


            btnQuitarFiltro.setOnClickListener {
                callUseCaseObtener()
            }
        }
    }
}