package com.morales.calcontrol

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.morales.calcontrol.data.AppDatabase
import com.morales.calcontrol.repository.EntradaAlimentoRepository
import com.morales.calcontrol.ui.EntradaAlimentoAdapter
import com.morales.calcontrol.ui.AgregarAlimentoDialogFragment
import com.morales.calcontrol.viewmodel.EntradaAlimentoViewModel
import com.morales.calcontrol.viewmodel.EntradaAlimentoViewModelFactory
import com.morales.calcontrol.notifications.NotificationHelper
import com.morales.calcontrol.utils.DialogHelper

class EntradaAlimentoActivity : AppCompatActivity() {

    private lateinit var notificationHelper: NotificationHelper
    private lateinit var dialogHelper: DialogHelper
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationHelper.scheduleDailyNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_alimento)

        // Inicializar Ayudantes (Refactorización)
        notificationHelper = NotificationHelper(this)
        dialogHelper = DialogHelper(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)

        // Configuración de Datos y ViewModel
        val dao = AppDatabase.getDatabase(this).entradaAlimentoDao()
        val repository = EntradaAlimentoRepository(dao)
        val viewModel: EntradaAlimentoViewModel by viewModels { 
            EntradaAlimentoViewModelFactory(repository) 
        }

        // 1. Gestión de Notificaciones y Permisos (Delegado a NotificationHelper)
        configurarNotificaciones()

        // 2. Configuración del Adaptador (CRUD visual delegado a DialogHelper)
        val adapter = EntradaAlimentoAdapter(
            onItemClick = { alimento ->
                AgregarAlimentoDialogFragment.newInstance(alimento) { 
                    viewModel.update(it) 
                }.show(supportFragmentManager, "EditarAlimento")
            },
            onItemLongClick = { alimento ->
                dialogHelper.mostrarDialogoEliminar(alimento, viewModel, recyclerView)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        // 3. Observar cambios para actualizar UI y lanzar Alertas (Delegado a NotificationHelper)
        viewModel.listaAlimentos.observe(this) { lista ->
            adapter.submitList(lista)
            
            val totalKcal = lista.sumOf { it.calorias_calculadas }
            val totalProtein = lista.sumOf { it.proteinas_calculadas }
            val totalCarbs = lista.sumOf { it.carbohidratos_calculados }
            val totalFat = lista.sumOf { it.grasas_calculadas }
            
            val meta = 2000.0
            val restante = if (meta - totalKcal > 0) meta - totalKcal else 0.0
            val porcentaje = ((totalKcal / meta) * 100).toInt().coerceAtMost(100)

            // Actualizar UI con los nuevos IDs del diseño moderno
            findViewById<TextView>(R.id.tvKcalMain).text = totalKcal.toInt().toString()
            findViewById<TextView>(R.id.tvKcalConsumed).text = "${totalKcal.toInt()} kcal"
            findViewById<TextView>(R.id.tvKcalRemaining).text = "${restante.toInt()} kcal"
            
            findViewById<TextView>(R.id.tvProteinValue).text = "${totalProtein.toInt()}g"
            findViewById<TextView>(R.id.tvCarbsValue).text = "${totalCarbs.toInt()}g"
            findViewById<TextView>(R.id.tvFatValue).text = "${totalFat.toInt()}g"
            
            findViewById<ProgressBar>(R.id.pbMain).progress = porcentaje
            findViewById<ProgressBar>(R.id.pbCircle).progress = porcentaje
            findViewById<TextView>(R.id.tvPercentageDone).text = "$porcentaje%\ndone"

            if (totalKcal > meta) {
                notificationHelper.enviarAlertaMetaSuperada(totalKcal)
            }
        }

        // 4. Agregar Alimento (Create) - Vinculado al botón del Header y al FAB
        findViewById<TextView>(R.id.tvAddMealHeader).setOnClickListener {
            AgregarAlimentoDialogFragment.newInstance { 
                viewModel.insert(it) 
            }.show(supportFragmentManager, "AgregarAlimento")
        }

        fabAgregar.setOnClickListener {
            AgregarAlimentoDialogFragment.newInstance { 
                viewModel.insert(it) 
            }.show(supportFragmentManager, "AgregarAlimento")
        }
    }

    private fun configurarNotificaciones() {
        if (!notificationHelper.hasNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Notificaciones de CalControl")
                    .setMessage("Para ayudarte a mantener tu hábito, necesitamos enviarte recordatorios y alertas de calorías.")
                    .setPositiveButton("Activar") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton("Ahora no", null)
                    .show()
            }
        } else {
            notificationHelper.scheduleDailyNotification()
        }
    }
}
