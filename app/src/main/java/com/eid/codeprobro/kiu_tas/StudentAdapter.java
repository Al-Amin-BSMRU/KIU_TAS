package com.eid.codeprobro.kiu_tas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<StudentModel> studentList;

    public StudentAdapter(List<StudentModel> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);
        holder.tvStudentName.setText(student.getName());
        holder.tvStudentId.setText("ID: " + student.getId());

        if (student.isPresent()) {
            holder.viewPresenceIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.viewPresenceIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId;
        View viewPresenceIndicator;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            viewPresenceIndicator = itemView.findViewById(R.id.viewPresenceIndicator);
        }
    }
}
