import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.studyshare.R

class SlideFragment : Fragment(R.layout.fragment_slides) {

    companion object {
        fun newInstance(imagemResId: Int, titulo: String, descricao: String): SlideFragment {
            val fragment = SlideFragment()
            val args = Bundle()
            args.putInt("imagemResId", imagemResId)
            args.putString("titulo", titulo)
            args.putString("descricao", descricao)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ViewImagem = view.findViewById<ImageView>(R.id.ViewImagem)
        val Titulo = view.findViewById<TextView>(R.id.Titulo)
        val Descricao = view.findViewById<TextView>(R.id.Descricao)

        arguments?.let {
            ViewImagem.setImageResource(it.getInt("imagemResId"))
            Titulo.text = it.getString("titulo")
            Descricao.text = it.getString("descricao")
        }
    }
}
